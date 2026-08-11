package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.Match;
import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.payload.response.StandingRowResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Turns a group's players and fixtures into a sorted table.
 *
 * Pure computation: no repositories, no entities written back. Nothing about a
 * standing is stored, so the table is always consistent with the results that
 * exist right now, whatever order they were entered in.
 *
 * Ranking chain: points -> game difference -> mini-table -> total games won.
 * Players still level after all of that share a position and are separated by
 * the admin.
 */
@Component
public class StandingsCalculator {

    /**
     * Running totals for one player. Mutable and package-private on purpose: it is
     * filled by accumulation, then read to build the immutable response row.
     */
    private static final class Stat {
        private final User player;
        private int played;
        private int scheduled;
        private int points;
        private int gamesWon;
        private int gamesLost;

        /**
         * Figures from the mini-table, i.e. counting only matches against the other
         * players this one is level with. Zero unless a tie-break was needed.
         */
        private int miniPoints;
        private int miniDifference;

        private Stat(User player) {
            this.player = player;
        }

        private int gameDifference() {
            return gamesWon - gamesLost;
        }
    }

    /**
     * @param players every player in the group, including those who have not played yet
     * @param matches every fixture of the group, played or not
     */
    public List<StandingRowResponse> calculate(List<User> players, List<Match> matches) {
        // seeded from the roster, not from the matches: a player with no fixtures
        // yet still belongs in the table
        Map<Long, Stat> stats = new LinkedHashMap<>();
        for (User player : players) {
            stats.put(player.getId(), new Stat(player));
        }

        accumulate(stats, matches);

        List<Stat> sorted = new ArrayList<>(stats.values());
        sorted.sort(Comparator
                .comparingInt((Stat s) -> s.points).reversed()
                .thenComparing(Comparator.comparingInt(Stat::gameDifference).reversed()));

        breakTies(sorted, matches);

        return toRows(sorted);
    }

    /** One pass over the fixtures; unplayed ones only add to {@code scheduled}. */
    private void accumulate(Map<Long, Stat> stats, List<Match> matches) {
        for (Match match : matches) {
            Stat first = statFor(stats, match.getPlayer1());
            Stat second = statFor(stats, match.getPlayer2());
            if (first == null || second == null) {
                continue; // an unfilled slot: nothing to count for either side
            }

            first.scheduled++;
            second.scheduled++;

            if (match.getWinner() == null) {
                continue; // scheduled but not played
            }

            int firstGames = match.getPlayer1Games();
            int secondGames = match.getPlayer2Games();

            first.played++;
            first.gamesWon += firstGames;
            first.gamesLost += secondGames;

            second.played++;
            second.gamesWon += secondGames;
            second.gamesLost += firstGames;

            // one point for a win, none for a loss
            if (match.getWinner().getId().equals(first.player.getId())) {
                first.points++;
            } else {
                second.points++;
            }
        }
    }

    /**
     * Cuts the sorted list into blocks of players level on points and game
     * difference, and orders each block by its mini-table.
     *
     * The figures are computed per block and stored on the {@link Stat}, never
     * compared pairwise: head-to-head is not transitive, so with A beating B,
     * B beating C and C beating A a pairwise comparator would invent an order
     * for what is really a three-way tie.
     */
    private void breakTies(List<Stat> sorted, List<Match> matches) {
        int blockStart = 0;
        while (blockStart < sorted.size()) {
            int blockEnd = blockStart + 1;
            while (blockEnd < sorted.size() && levelOnMainCriteria(sorted.get(blockStart), sorted.get(blockEnd))) {
                blockEnd++;
            }

            if (blockEnd - blockStart > 1) {
                List<Stat> block = sorted.subList(blockStart, blockEnd);
                computeMiniTable(block, matches);
                block.sort(Comparator
                        .comparingInt((Stat s) -> s.miniPoints).reversed()
                        .thenComparing(Comparator.comparingInt((Stat s) -> s.miniDifference).reversed())
                        .thenComparing(Comparator.comparingInt((Stat s) -> s.gamesWon).reversed()));
            }

            blockStart = blockEnd;
        }
    }

    /** Replays the block's internal matches only, and stores the result on each Stat. */
    private void computeMiniTable(List<Stat> block, List<Match> matches) {
        Set<Long> blockIds = block.stream().map(s -> s.player.getId()).collect(Collectors.toSet());
        Map<Long, Stat> byId = block.stream().collect(Collectors.toMap(s -> s.player.getId(), s -> s));

        for (Match match : matches) {
            if (match.getWinner() == null || match.getPlayer1() == null || match.getPlayer2() == null) {
                continue;
            }
            Long firstId = match.getPlayer1().getId();
            Long secondId = match.getPlayer2().getId();
            if (!blockIds.contains(firstId) || !blockIds.contains(secondId)) {
                continue; // one of them is outside the tie, so the match is not part of it
            }

            Stat first = byId.get(firstId);
            Stat second = byId.get(secondId);
            int firstGames = match.getPlayer1Games();
            int secondGames = match.getPlayer2Games();

            first.miniDifference += firstGames - secondGames;
            second.miniDifference += secondGames - firstGames;

            if (match.getWinner().getId().equals(firstId)) {
                first.miniPoints++;
            } else {
                second.miniPoints++;
            }
        }
    }

    private List<StandingRowResponse> toRows(List<Stat> sorted) {
        List<StandingRowResponse> rows = new ArrayList<>(sorted.size());
        int position = 1;

        for (int i = 0; i < sorted.size(); i++) {
            Stat stat = sorted.get(i);
            // standard competition ranking: after a shared position the next player
            // takes the place their index implies, so 1, 2, 2, 4
            if (i > 0 && !completelyLevel(sorted.get(i - 1), stat)) {
                position = i + 1;
            }

            rows.add(StandingRowResponse.builder()
                    .position(position)
                    .playerId(stat.player.getId())
                    .playerName(stat.player.getFirstName() + " " + stat.player.getLastName())
                    .played(stat.played)
                    .scheduled(stat.scheduled)
                    .points(stat.points)
                    .gamesWon(stat.gamesWon)
                    .gamesLost(stat.gamesLost)
                    .gameDifference(stat.gameDifference())
                    .build());
        }

        return rows;
    }

    private boolean levelOnMainCriteria(Stat a, Stat b) {
        return a.points == b.points && a.gameDifference() == b.gameDifference();
    }

    /** Level on every criterion the system can decide; only the admin can split these. */
    private boolean completelyLevel(Stat a, Stat b) {
        return levelOnMainCriteria(a, b)
                && a.miniPoints == b.miniPoints
                && a.miniDifference == b.miniDifference
                && a.gamesWon == b.gamesWon;
    }

    private Stat statFor(Map<Long, Stat> stats, User player) {
        return player == null ? null : stats.get(player.getId());
    }
}
