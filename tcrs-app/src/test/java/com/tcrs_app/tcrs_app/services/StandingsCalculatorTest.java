package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.Match;
import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.payload.response.StandingRowResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Plain unit tests - the calculator touches no repositories, so no Spring context
 * is needed and the cases can be written out by hand.
 */
class StandingsCalculatorTest {

    private final StandingsCalculator calculator = new StandingsCalculator();

    private static User player(long id, String firstName) {
        return User.builder().id(id).firstName(firstName).lastName("Test").build();
    }

    /** A fixture with a result. The first named player is the one who won. */
    private static Match played(User winner, int winnerGames, User loser, int loserGames) {
        Match match = new Match();
        match.setRoundNumber(1);
        match.setPlayer1(winner);
        match.setPlayer2(loser);
        match.setPlayer1Games(winnerGames);
        match.setPlayer2Games(loserGames);
        match.setWinner(winner);
        return match;
    }

    /** A fixture that exists but has not been played. */
    private static Match scheduled(User first, User second) {
        Match match = new Match();
        match.setRoundNumber(1);
        match.setPlayer1(first);
        match.setPlayer2(second);
        return match;
    }

    private static List<Integer> positions(List<StandingRowResponse> rows) {
        return rows.stream().map(StandingRowResponse::getPosition).toList();
    }

    private static List<Long> playerIds(List<StandingRowResponse> rows) {
        return rows.stream().map(StandingRowResponse::getPlayerId).toList();
    }

    @Test
    void groupWithNoResultsYetListsEveryoneLevel() {
        User ana = player(1, "Ana");
        User bruno = player(2, "Bruno");
        User cvita = player(3, "Cvita");

        List<StandingRowResponse> rows = calculator.calculate(
                List.of(ana, bruno, cvita),
                List.of(scheduled(ana, bruno), scheduled(bruno, cvita), scheduled(ana, cvita)));

        assertEquals(3, rows.size());
        assertEquals(List.of(1, 1, 1), positions(rows));
        rows.forEach(row -> {
            assertEquals(0, row.getPlayed());
            assertEquals(2, row.getScheduled(), "each player has two fixtures waiting");
            assertEquals(0, row.getPoints());
            assertEquals(0, row.getGameDifference());
        });
    }

    @Test
    void pointsSeparatePlayersWhenThereIsNoTie() {
        User ana = player(1, "Ana");
        User bruno = player(2, "Bruno");
        User cvita = player(3, "Cvita");

        List<StandingRowResponse> rows = calculator.calculate(
                List.of(cvita, bruno, ana), // deliberately not in finishing order
                List.of(
                        played(ana, 9, bruno, 4),
                        played(ana, 9, cvita, 2),
                        played(bruno, 9, cvita, 6)));

        assertEquals(List.of(1L, 2L, 3L), playerIds(rows));
        assertEquals(List.of(1, 2, 3), positions(rows));
        assertEquals(2, rows.get(0).getPoints());
        assertEquals(18, rows.get(0).getGamesWon());
        assertEquals(6, rows.get(0).getGamesLost());
        assertEquals(12, rows.get(0).getGameDifference());
        assertEquals(2, rows.get(0).getPlayed());
    }

    @Test
    void perfectThreeWayCycleSharesFirstPlace() {
        User ana = player(1, "Ana");
        User bruno = player(2, "Bruno");
        User cvita = player(3, "Cvita");

        // A beats B, B beats C, C beats A, all by the same margin: nothing can
        // separate them, and head-to-head must not pretend otherwise
        List<StandingRowResponse> rows = calculator.calculate(
                List.of(ana, bruno, cvita),
                List.of(
                        played(ana, 9, bruno, 7),
                        played(bruno, 9, cvita, 7),
                        played(cvita, 9, ana, 7)));

        assertEquals(List.of(1, 1, 1), positions(rows));
        rows.forEach(row -> {
            assertEquals(1, row.getPoints());
            assertEquals(0, row.getGameDifference());
        });
    }

    @Test
    void miniTableSeparatesPlayersLevelOnPointsAndGameDifference() {
        User ana = player(1, "Ana");
        User bruno = player(2, "Bruno");
        User cvita = player(3, "Cvita");
        User dario = player(4, "Dario");

        /*
         * Ana, Bruno and Cvita all finish on 2 points and +5 game difference, so the
         * tie-break has to look at their matches with each other only. There they
         * form a cycle with margins +2 / +2 / +1, giving mini differences of
         * +1 / 0 / -1 -> Ana, Bruno, Cvita.
         *
         * Their overall games won are 26 / 25 / 25, so a calculator that skipped the
         * mini-table and went straight to games won would tie Bruno with Cvita.
         */
        List<StandingRowResponse> rows = calculator.calculate(
                List.of(dario, cvita, bruno, ana),
                List.of(
                        played(ana, 9, bruno, 7),
                        played(bruno, 9, cvita, 7),
                        played(cvita, 9, ana, 8),
                        played(ana, 9, dario, 5),
                        played(bruno, 9, dario, 4),
                        played(cvita, 9, dario, 3)));

        assertEquals(List.of(1L, 2L, 3L, 4L), playerIds(rows));
        assertEquals(List.of(1, 2, 3, 4), positions(rows));

        rows.subList(0, 3).forEach(row -> {
            assertEquals(2, row.getPoints());
            assertEquals(5, row.getGameDifference());
        });
        assertEquals(0, rows.get(3).getPoints());
        assertEquals(-15, rows.get(3).getGameDifference());
    }

    @Test
    void unplayedMatchesCountAsScheduledOnly() {
        User ana = player(1, "Ana");
        User bruno = player(2, "Bruno");
        User cvita = player(3, "Cvita");

        List<StandingRowResponse> rows = calculator.calculate(
                List.of(ana, bruno, cvita),
                List.of(
                        played(ana, 9, bruno, 4),
                        scheduled(ana, cvita),
                        scheduled(bruno, cvita)));

        StandingRowResponse leader = rows.get(0);
        assertEquals(1L, leader.getPlayerId());
        assertEquals(1, leader.getPlayed());
        assertEquals(2, leader.getScheduled());

        StandingRowResponse cvitaRow = rows.stream()
                .filter(row -> row.getPlayerId() == 3L).findFirst().orElseThrow();
        assertEquals(0, cvitaRow.getPlayed());
        assertEquals(2, cvitaRow.getScheduled());
        // still ahead of Bruno, who has an actual loss on his record
        assertEquals(2, cvitaRow.getPosition());
    }
}
