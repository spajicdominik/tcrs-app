package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.GroupPlayer;
import com.tcrs_app.tcrs_app.entities.Match;
import com.tcrs_app.tcrs_app.entities.Tournament;
import com.tcrs_app.tcrs_app.entities.TournamentGroup;
import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.enums.AppUserRole;
import com.tcrs_app.tcrs_app.enums.AppUserStatus;
import com.tcrs_app.tcrs_app.enums.EliminationFormat;
import com.tcrs_app.tcrs_app.enums.TournamentPhase;
import com.tcrs_app.tcrs_app.exception.TournamentException;
import com.tcrs_app.tcrs_app.payload.request.CreateTournamentRequest;
import com.tcrs_app.tcrs_app.payload.request.MatchInsertRequest;
import com.tcrs_app.tcrs_app.payload.request.TournamentOptionsRequest;
import com.tcrs_app.tcrs_app.payload.response.*;
import com.tcrs_app.tcrs_app.repositories.GroupPlayerRepository;
import com.tcrs_app.tcrs_app.repositories.MatchRepository;
import com.tcrs_app.tcrs_app.repositories.TournamentGroupRepository;
import com.tcrs_app.tcrs_app.repositories.TournamentRepository;
import com.tcrs_app.tcrs_app.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService{

    private final TournamentRepository tournamentRepository;
    private final TournamentGroupRepository tournamentGroupRepository;
    private final GroupPlayerRepository groupPlayerRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final StandingsCalculator standingsCalculator;

    @Override
    public TournamentResponse createTournament(CreateTournamentRequest request, User currentUser) {
        List<Tournament> activeTournaments = tournamentRepository.findByPhaseNot(TournamentPhase.CLOSED);
        if (!activeTournaments.isEmpty()) {
            throw new TournamentException("There is a currently active tournament.", HttpStatus.BAD_REQUEST);
        }

        // group sizes are derived, not taken from the request, so uneven splits
        // (23 players into 4 groups -> 6,6,6,5) work without any dummy players
        List<Integer> groupSizes = splitIntoGroups(request.getPlayerIds().size(), request.getNumberOfGroups());
        validateLayout(request, groupSizes);

        List<User> players = loadPlayers(request.getPlayerIds());

        Tournament tournament = tournamentRepository.save(Tournament.builder()
                .name(request.getName())
                .phase(TournamentPhase.GROUP)
                .qualifiersPerGroup(request.getQualifiersPerGroup())
                // both NOT NULL: Hibernate includes mapped columns in the INSERT, so
                // the DB defaults never apply and they have to be set explicitly here
                .eliminationFormat(formatOf(request.getEliminationFormat()))
                .currentRound(1)
                .build());

        // one group per requested group, named A, B, C ...
        List<TournamentGroup> groups = new ArrayList<>();
        for (int i = 0; i < request.getNumberOfGroups(); i++) {
            TournamentGroup group = new TournamentGroup();
            group.setTournament(tournament);
            group.setName(String.valueOf((char) ('A' + i)));
            groups.add(tournamentGroupRepository.save(group));
        }

        // random draw: shuffle once, then deal the players out like cards, which
        // reproduces the sizes computed above
        Collections.shuffle(players);
        for (int i = 0; i < players.size(); i++) {
            GroupPlayer groupPlayer = new GroupPlayer();
            groupPlayer.setGroup(groups.get(i % groups.size()));
            groupPlayer.setUser(players.get(i));
            groupPlayerRepository.save(groupPlayer);
        }

        return TournamentResponse.builder()
                .id(tournament.getId())
                .name(tournament.getName())
                .numberOfGroups(groups.size())
                .build();
    }

    /** Older clients omit the field entirely; the original format stays the default. */
    private EliminationFormat formatOf(EliminationFormat requested) {
        return requested == null ? EliminationFormat.SINGLE_BRACKET : requested;
    }

    private void validateLayout(CreateTournamentRequest request, List<Integer> groupSizes) {
        if (new HashSet<>(request.getPlayerIds()).size() != request.getPlayerIds().size()) {
            throw new TournamentException("The same player was selected more than once.", HttpStatus.BAD_REQUEST);
        }

        int smallestGroup = groupSizes.get(groupSizes.size() - 1);
        if (smallestGroup < MIN_GROUP_SIZE) {
            throw new TournamentException(
                    "Each group needs at least " + MIN_GROUP_SIZE + " players.", HttpStatus.BAD_REQUEST);
        }

        // every group must be able to supply the same number of qualifiers
        if (request.getQualifiersPerGroup() > smallestGroup) {
            throw new TournamentException(
                    "The smallest group has " + smallestGroup + " players and cannot supply "
                            + request.getQualifiersPerGroup() + " qualifiers.", HttpStatus.BAD_REQUEST);
        }

        // the elimination bracket halves each round, so the qualifier total must be a
        // power of two (2, 4, 8, 16 ...) or the rounds cannot be paired up
        int bracketSize = request.getQualifiersPerGroup() * request.getNumberOfGroups();
        if (bracketSize < 2 || Integer.bitCount(bracketSize) != 1) {
            throw new TournamentException(
                    "Total qualifiers must be a power of two, but was " + bracketSize + ".",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private List<User> loadPlayers(List<Long> playerIds) {
        List<User> players = userRepository.findAllById(playerIds);
        if (players.size() != playerIds.size()) {
            throw new TournamentException("One or more selected players do not exist.", HttpStatus.NOT_FOUND);
        }
        if (players.stream().anyMatch(p -> p.getStatus() != AppUserStatus.ACTIVE)) {
            throw new TournamentException("All selected players must be active.", HttpStatus.BAD_REQUEST);
        }
        // findAllById may return a fixed-size list; copy so it can be shuffled
        return new ArrayList<>(players);
    }

    /** A group smaller than this cannot produce a meaningful round-robin table. */
    private static final int MIN_GROUP_SIZE = 3;

    @Override
    public TournamentOptionsResponse generateTournamentOptions(TournamentOptionsRequest request, User currentUser) {
        int numberOfPlayers = request.getPlayerIds().size();
        List<TournamentOption> options = new ArrayList<>();

        /*
         * qualifiersPerGroup * numberOfGroups must be a power of two (the bracket halves
         * each round). An integer q can never turn an odd factor of g into a power of two,
         * so numberOfGroups itself has to be a power of two - hence g doubles each step.
         */
        for (int groups = 1; groups <= numberOfPlayers; groups *= 2) {
            List<Integer> groupSizes = splitIntoGroups(numberOfPlayers, groups);
            int smallestGroup = groupSizes.get(groupSizes.size() - 1);
            if (smallestGroup < MIN_GROUP_SIZE) {
                break; // groups only get smaller from here
            }

            // every group must be able to supply the same number of qualifiers,
            // so cap by the SMALLEST group when the split is uneven
            List<Integer> qualifierOptions = new ArrayList<>();
            for (int qualifiers = 1; qualifiers <= smallestGroup; qualifiers++) {
                int bracketSize = qualifiers * groups;
                if (bracketSize >= 2 && Integer.bitCount(bracketSize) == 1) {
                    qualifierOptions.add(qualifiers);
                }
            }
            if (qualifierOptions.isEmpty()) {
                continue;
            }

            options.add(TournamentOption.builder()
                    .numberOfGroups(groups)
                    .groupSizes(groupSizes)
                    .evenGroups(smallestGroup == groupSizes.get(0))
                    .qualifiersPerGroupOptions(qualifierOptions)
                    .build());
        }

        return TournamentOptionsResponse.builder()
                .numberOfPlayers(numberOfPlayers)
                .options(options)
                .build();
    }

    @Override
    public TournamentMatchesResponse generateTournamentMatches(@Valid Long id, User currentUser) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentException("Tournament not found.", HttpStatus.NOT_FOUND));

        if (tournament.getPhase() != TournamentPhase.GROUP) {
            throw new TournamentException(
                    "Group fixtures can only be generated during the group phase.", HttpStatus.BAD_REQUEST);
        }

        List<TournamentGroup> groups = tournamentGroupRepository.findByTournamentOrderByNameAsc(tournament);
        if (groups.isEmpty()) {
            throw new TournamentException("This tournament has no groups.", HttpStatus.BAD_REQUEST);
        }
        if (matchRepository.existsByGroupIn(groups)) {
            throw new TournamentException(
                    "Matches have already been generated for this tournament.", HttpStatus.CONFLICT);
        }

        for (TournamentGroup group : groups) {
            List<User> players = groupPlayerRepository.findByGroup(group).stream()
                    .map(GroupPlayer::getUser)
                    .collect(Collectors.toCollection(ArrayList::new));
            createRoundRobin(tournament, group, players);
        }

        // every round is playable from the start; this only records where the
        // competition nominally is
        tournament.setCurrentRound(1);

        return buildMatchesResponse(tournament, groups, currentUser);
    }

    @Override
    public TournamentStandingsResponse getCurrentTournamentStandings(Long id, User currentUser) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentException("Tournament not found.", HttpStatus.NOT_FOUND));

        List<GroupStandingsResponse> groupStandings = buildAllGroupStandings(tournament);

        return TournamentStandingsResponse.builder()
                .tournamentId(tournament.getId())
                .phase(tournament.getPhase().name())
                .readyForElimination(readyForElimination(tournament, groupStandings))
                .groups(groupStandings)
                .build();
    }

    @Override
    public ActiveTournamentResponse getActiveTournament(User currentUser) {
        // createTournament allows only one open season at a time, so there is at most one
        return tournamentRepository.findByPhaseNot(TournamentPhase.CLOSED).stream()
                .findFirst()
                .map(tournament -> ActiveTournamentResponse.builder()
                        .active(true)
                        .id(tournament.getId())
                        .name(tournament.getName())
                        .phase(tournament.getPhase().name())
                        .canFinish(isCompleted(tournament))
                        .build())
                .orElseGet(() -> ActiveTournamentResponse.builder()
                        .active(false)
                        .canFinish(false)
                        .build());
    }

    @Override
    public ActiveTournamentResponse finishTournament(Long id, User currentUser) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentException("Tournament not found.", HttpStatus.NOT_FOUND));

        if (tournament.getPhase() == TournamentPhase.CLOSED) {
            throw new TournamentException("This tournament is already closed.", HttpStatus.BAD_REQUEST);
        }
        if (!isCompleted(tournament)) {
            throw new TournamentException(
                    "The final has not been played yet, so the tournament cannot be closed.",
                    HttpStatus.BAD_REQUEST);
        }

        // CLOSED, not FINISHED: createTournament refuses while any tournament is not
        // closed, so this is the state that frees the club to start the next season
        tournament.setPhase(TournamentPhase.CLOSED);
        tournamentRepository.save(tournament);

        return getActiveTournament(currentUser);
    }

    /**
     * A tournament is over when its bracket exists and every final has a winner. A final
     * is an elimination match that feeds nothing - PER_POSITION has one per place.
     */
    private boolean isCompleted(Tournament tournament) {
        List<Match> finals = matchRepository.findByTournamentOrderByRoundNumberAscIdAsc(tournament).stream()
                .filter(match -> match.getGroup() == null)
                .filter(match -> match.getNextMatch() == null)
                .toList();

        return !finals.isEmpty() && finals.stream().allMatch(match -> match.getWinner() != null);
    }

    private List<GroupStandingsResponse> buildAllGroupStandings(Tournament tournament) {
        return tournamentGroupRepository.findByTournamentOrderByNameAsc(tournament).stream()
                .map(group -> buildGroupStandings(
                        group, tournament.getQualifiersPerGroup(), formatOf(tournament.getEliminationFormat())))
                .toList();
    }

    /**
     * The two guards are not redundant: allMatch on an empty stream returns true, so a
     * tournament with no groups would otherwise claim to be ready, and one that has
     * already advanced would offer to advance a second time.
     */
    private boolean readyForElimination(Tournament tournament, List<GroupStandingsResponse> groupStandings) {
        return !groupStandings.isEmpty()
                && tournament.getPhase() == TournamentPhase.GROUP
                && groupStandings.stream()
                        .allMatch(g -> g.getComplete() && g.getQualifiersDecided());
    }

    @Override
    @Transactional
    public MatchInsertResponse insertTournamentMatchResult(MatchInsertRequest request, User currentUser) {
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new TournamentException("Match not found.", HttpStatus.NOT_FOUND));

        if (!canEnterScore(match, currentUser)) {
            throw new TournamentException("You are not allowed to enter score for this match.", HttpStatus.FORBIDDEN);
        }

        Integer p1Games = request.getPlayer1Games();
        Integer p2Games = request.getPlayer2Games();

        if (p1Games == null || p2Games == null) {
            throw new TournamentException("Both player games must be provided.", HttpStatus.BAD_REQUEST);
        }

        if (p1Games < 0 || p1Games > 9 || p2Games < 0 || p2Games > 9) {
            throw new TournamentException("Games must be between 0 and 9.", HttpStatus.BAD_REQUEST);
        }

        if (p1Games.equals(p2Games)) {
            throw new TournamentException("Matches cannot end in a tie.", HttpStatus.BAD_REQUEST);
        }

        if (p1Games < 9 && p2Games < 9) {
            throw new TournamentException("One player must have 9 games.", HttpStatus.BAD_REQUEST);
        }

        match.setPlayer1Games(p1Games);
        match.setPlayer2Games(p2Games);
        match.setWinner(p1Games > p2Games ? match.getPlayer1() : match.getPlayer2());
        match.setScoreEnteredBy(currentUser);

        matchRepository.save(match);

        return MatchInsertResponse.builder()
                .match(toMatchResponse(match, currentUser))
                .build();
    }

    @Override
    public List<MatchRoundsResponse> getTorunamentMatchesByRounds(Long id, User currentUser) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentException("Tournament not found.", HttpStatus.NOT_FOUND));

        List<TournamentGroup> groups = tournamentGroupRepository.findByTournamentOrderByNameAsc(tournament);

        return matchRepository.findByGroupInOrderByRoundNumberAscIdAsc(groups).stream()
                .collect(Collectors.groupingBy(Match::getRoundNumber, TreeMap::new, Collectors.toList()))
                .entrySet().stream()
                .map(entry -> MatchRoundsResponse.builder()
                        .roundNumber(entry.getKey())
                        .matches(entry.getValue().stream()
                                .collect(Collectors.groupingBy(
                                        m -> m.getGroup() != null ? m.getGroup().getId() : -1L,
                                        LinkedHashMap::new,
                                        Collectors.toList()
                                ))
                                .entrySet().stream()
                                .map(groupEntry -> {
                                    Match firstMatch = groupEntry.getValue().get(0);
                                    return CurrentRoundGroupMatches.builder()
                                            .groupId(groupEntry.getKey() != -1 ? groupEntry.getKey().intValue() : null)
                                            .groupName(firstMatch.getGroup() != null ? firstMatch.getGroup().getName() : null)
                                            .matches(groupEntry.getValue().stream()
                                                    .map(match -> toMatchResponse(match, currentUser))
                                                    .toList())
                                            .build();
                                })
                                .toList())
                        .build())
                .toList();
    }



    private GroupStandingsResponse buildGroupStandings(
            TournamentGroup group, Integer qualifiersPerGroup, EliminationFormat format) {

        List<User> players = groupPlayerRepository.findByGroup(group).stream()
                .map(GroupPlayer::getUser)
                .toList();
        List<Match> matches = matchRepository.findByGroup(group);
        List<StandingRowResponse> rows = standingsCalculator.calculate(players, matches);

        // derived, not stored: the group is done when nothing is left unresolved
        int matchesRemaining = (int) matches.stream().filter(m -> m.getWinner() == null).count();

        return GroupStandingsResponse.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .qualifiersPerGroup(qualifiersPerGroup)
                .complete(!matches.isEmpty() && matchesRemaining == 0)
                .matchesRemaining(matchesRemaining)
                .qualifiersDecided(qualifiersDecided(rows, qualifiersPerGroup, format))
                .rows(rows)
                .build();
    }

    /**
     * Whether the group's qualifiers can be named. Only a tie AT the cut line blocks it -
     * a three-way tie for third place is irrelevant when two players go through.
     */
    private boolean qualifiersDecided(
            List<StandingRowResponse> rows, int qualifiersPerGroup, EliminationFormat format) {

        /*
         * With a bracket for every place, the finishing position decides WHICH bracket a
         * player enters, so a tie for fourth is exactly as blocking as a tie for first.
         * There is no cut line to be on the right side of - every place must be settled.
         */
        if (format == EliminationFormat.PER_POSITION) {
            return rows.stream().map(StandingRowResponse::getPosition).distinct().count() == rows.size();
        }

        // no cut line to straddle: the whole group goes through
        if (rows.size() <= qualifiersPerGroup) {
            return true;
        }

        // equals, not ==: position is a boxed Integer and would compare by reference
        return !rows.get(qualifiersPerGroup - 1).getPosition()
                .equals(rows.get(qualifiersPerGroup).getPosition());
    }

    /**
     * Circle method: everyone plays everyone once.
     *
     * The first player is held still while the rest rotate one place each round, so
     * round r pairs position i with position n-1-i. With an odd number of players a
     * null "bye" is added to make the count even - whoever draws it simply rests that
     * round, and no match row is written for the pairing.
     */
    private void createRoundRobin(Tournament tournament, TournamentGroup group, List<User> players) {
        List<User> slots = new ArrayList<>(players);
        if (slots.size() % 2 != 0) {
            slots.add(null); // bye - a scheduling placeholder, never persisted
        }

        int size = slots.size();
        int rounds = size - 1;
        int matchesPerRound = size / 2;

        for (int round = 1; round <= rounds; round++) {
            for (int i = 0; i < matchesPerRound; i++) {
                User player1 = slots.get(i);
                User player2 = slots.get(size - 1 - i);

                if (player1 != null && player2 != null) {
                    Match match = new Match();
                    // NOT NULL: a group match is reachable both ways, but the column is
                    // what elimination matches will rely on, so it is always set
                    match.setTournament(tournament);
                    match.setGroup(group);
                    match.setRoundNumber(round);
                    match.setPlayer1(player1);
                    match.setPlayer2(player2);
                    matchRepository.save(match);
                }
            }

            // rotate every slot except the first
            List<User> rotating = new ArrayList<>(slots.subList(1, size));
            Collections.rotate(rotating, 1);
            for (int i = 1; i < size; i++) {
                slots.set(i, rotating.get(i - 1));
            }
        }
    }

    private TournamentMatchesResponse buildMatchesResponse(
            Tournament tournament, List<TournamentGroup> groups, User currentUser) {

        List<TournamentRoundMatches> rounds =
                matchRepository.findByGroupInOrderByRoundNumberAscIdAsc(groups).stream()
                        .collect(Collectors.groupingBy(Match::getRoundNumber, TreeMap::new, Collectors.toList()))
                        .entrySet().stream()
                        .map(entry -> TournamentRoundMatches.builder()
                                .roundNumber(entry.getKey())
                                .matches(entry.getValue().stream()
                                        .map(match -> toMatchResponse(match, currentUser))
                                        .toList())
                                .build())
                        .toList();

        return TournamentMatchesResponse.builder()
                .tournamentId(tournament.getId())
                .phase(tournament.getPhase().name())
                .currentRound(tournament.getCurrentRound())
                .rounds(rounds)
                .build();
    }

    private MatchResponse toMatchResponse(Match match, User currentUser) {
        TournamentGroup group = match.getGroup();
        User player1 = match.getPlayer1();
        User player2 = match.getPlayer2();

        return MatchResponse.builder()
                .id(match.getId())
                .roundNumber(match.getRoundNumber())
                .groupId(group != null ? group.getId() : null)
                .groupName(group != null ? group.getName() : null)
                .nextMatchId(match.getNextMatch() != null ? match.getNextMatch().getId() : null)
                .player1Id(player1 != null ? player1.getId() : null)
                .player1Name(fullName(player1))
                .player2Id(player2 != null ? player2.getId() : null)
                .player2Name(fullName(player2))
                .player1Games(match.getPlayer1Games())
                .player2Games(match.getPlayer2Games())
                .winnerId(match.getWinner() != null ? match.getWinner().getId() : null)
                .scoreEnteredById(match.getScoreEnteredBy() != null ? match.getScoreEnteredBy().getId() : null)
                .canEnterScore(canEnterScore(match, currentUser))
                .build();
    }

    private String fullName(User user) {
        return user == null ? null : user.getFirstName() + " " + user.getLastName();
    }

    /**
     * Admins may always edit. A player may score their own match while it is unplayed,
     * and afterwards only if they were the one who entered the result. The round the
     * match belongs to is irrelevant - matches are often played late.
     */
    private boolean canEnterScore(Match match, User currentUser) {
        if (currentUser.getRole() == AppUserRole.ADMIN) {
            return true;
        }
        Long userId = currentUser.getId();
        boolean isParticipant =
                (match.getPlayer1() != null && match.getPlayer1().getId().equals(userId))
                        || (match.getPlayer2() != null && match.getPlayer2().getId().equals(userId));
        if (!isParticipant) {
            return false;
        }
        return match.getScoreEnteredBy() == null || match.getScoreEnteredBy().getId().equals(userId);
    }

    /**
     * Spreads players as evenly as possible, largest group first.
     * 23 players in 4 groups -> [6, 6, 6, 5].
     */
    private List<Integer> splitIntoGroups(int numberOfPlayers, int numberOfGroups) {
        int base = numberOfPlayers / numberOfGroups;
        int remainder = numberOfPlayers % numberOfGroups;

        List<Integer> sizes = new ArrayList<>();
        for (int i = 0; i < numberOfGroups; i++) {
            sizes.add(i < remainder ? base + 1 : base);
        }
        return sizes;
    }
}
