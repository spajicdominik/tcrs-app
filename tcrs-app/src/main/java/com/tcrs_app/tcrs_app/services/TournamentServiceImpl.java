package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.GroupPlayer;
import com.tcrs_app.tcrs_app.entities.Tournament;
import com.tcrs_app.tcrs_app.entities.TournamentGroup;
import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.enums.AppUserStatus;
import com.tcrs_app.tcrs_app.enums.TournamentPhase;
import com.tcrs_app.tcrs_app.exception.TournamentException;
import com.tcrs_app.tcrs_app.payload.request.CreateTournamentRequest;
import com.tcrs_app.tcrs_app.payload.request.TournamentOptionsRequest;
import com.tcrs_app.tcrs_app.payload.response.TournamentMatchesResponse;
import com.tcrs_app.tcrs_app.payload.response.TournamentOption;
import com.tcrs_app.tcrs_app.payload.response.TournamentOptionsResponse;
import com.tcrs_app.tcrs_app.payload.response.TournamentResponse;
import com.tcrs_app.tcrs_app.repositories.GroupPlayerRepository;
import com.tcrs_app.tcrs_app.repositories.TournamentGroupRepository;
import com.tcrs_app.tcrs_app.repositories.TournamentRepository;
import com.tcrs_app.tcrs_app.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService{

    private final TournamentRepository tournamentRepository;
    private final TournamentGroupRepository tournamentGroupRepository;
    private final GroupPlayerRepository groupPlayerRepository;
    private final UserRepository userRepository;

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
        return null;
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
