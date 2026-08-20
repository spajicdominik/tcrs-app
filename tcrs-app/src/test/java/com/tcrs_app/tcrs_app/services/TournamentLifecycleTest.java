package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.Match;
import com.tcrs_app.tcrs_app.entities.Tournament;
import com.tcrs_app.tcrs_app.entities.TournamentGroup;
import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.enums.EliminationFormat;
import com.tcrs_app.tcrs_app.enums.TournamentPhase;
import com.tcrs_app.tcrs_app.exception.TournamentException;
import com.tcrs_app.tcrs_app.payload.response.ActiveTournamentResponse;
import com.tcrs_app.tcrs_app.repositories.MatchRepository;
import com.tcrs_app.tcrs_app.repositories.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Covers GET /tournaments/active and POST /tournaments/{id}/finish - the two things the
 * home screen needs to decide whether a new season may be created.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TournamentLifecycleTest {

    @Mock private TournamentRepository tournamentRepository;
    @Mock private MatchRepository matchRepository;
    @InjectMocks private TournamentServiceImpl tournamentService;

    private Tournament tournament;
    private User admin;
    private User ana;
    private User bruno;

    @BeforeEach
    void setUp() {
        tournament = Tournament.builder()
                .id(1L).name("Ljeto 2026").phase(TournamentPhase.ELIMINATION)
                .qualifiersPerGroup(2)
                .eliminationFormat(EliminationFormat.SINGLE_BRACKET)
                .currentRound(4)
                .build();

        admin = User.builder().id(1L).firstName("Admin").lastName("A").build();
        ana = User.builder().id(2L).firstName("Ana").lastName("T").build();
        bruno = User.builder().id(3L).firstName("Bruno").lastName("T").build();

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        // answers against the tournament's CURRENT phase, the way the real query would;
        // a fixed thenReturn would keep handing back a season after it was closed
        when(tournamentRepository.findByPhaseNot(TournamentPhase.CLOSED)).thenAnswer(invocation ->
                tournament.getPhase() == TournamentPhase.CLOSED ? List.of() : List.of(tournament));
    }

    private static Match eliminationMatch(long id, Match nextMatch, User winner) {
        Match match = new Match();
        match.setId(id);
        match.setRoundNumber(4);
        match.setNextMatch(nextMatch);
        match.setWinner(winner);
        return match;
    }

    private void givenBracket(User finalWinner) {
        Match theFinal = eliminationMatch(30L, null, finalWinner);
        Match upperSemi = eliminationMatch(10L, theFinal, ana);
        Match lowerSemi = eliminationMatch(20L, theFinal, bruno);
        when(matchRepository.findByTournamentOrderByRoundNumberAscIdAsc(tournament))
                .thenReturn(List.of(upperSemi, lowerSemi, theFinal));
    }

    private void givenGroupPhaseOnly() {
        TournamentGroup group = new TournamentGroup();
        group.setId(100L);
        Match groupMatch = new Match();
        groupMatch.setId(1L);
        groupMatch.setGroup(group);
        groupMatch.setRoundNumber(1);
        groupMatch.setWinner(ana);
        when(matchRepository.findByTournamentOrderByRoundNumberAscIdAsc(tournament))
                .thenReturn(List.of(groupMatch));
    }

    @Test
    void anOpenSeasonIsReportedAsActive() {
        givenBracket(null);
        ActiveTournamentResponse response = tournamentService.getActiveTournament(admin);

        assertTrue(response.getActive());
        assertEquals(1L, response.getId());
        assertEquals("Ljeto 2026", response.getName());
        assertEquals("ELIMINATION", response.getPhase());
        assertFalse(response.getCanFinish(), "the final has not been played");
    }

    @Test
    void noOpenSeasonMeansANewOneMayBeCreated() {
        when(tournamentRepository.findByPhaseNot(TournamentPhase.CLOSED)).thenReturn(List.of());
        ActiveTournamentResponse response = tournamentService.getActiveTournament(admin);

        assertFalse(response.getActive());
        assertFalse(response.getCanFinish());
        assertNull(response.getId());
    }

    @Test
    void aSeasonStillInTheGroupPhaseCannotBeFinished() {
        givenGroupPhaseOnly();
        assertFalse(tournamentService.getActiveTournament(admin).getCanFinish());
    }

    @Test
    void aDecidedFinalMakesTheSeasonFinishable() {
        givenBracket(ana);
        assertTrue(tournamentService.getActiveTournament(admin).getCanFinish());
    }

    @Test
    void finishingClosesTheSeasonAndFreesTheNextOne() {
        givenBracket(ana);
        ActiveTournamentResponse response = tournamentService.finishTournament(1L, admin);

        assertEquals(TournamentPhase.CLOSED, tournament.getPhase());
        assertFalse(response.getActive(), "the response reflects the state after closing");
    }

    @Test
    void aSeasonWhoseFinalIsUnplayedCannotBeClosed() {
        givenBracket(null);
        TournamentException thrown = assertThrows(TournamentException.class,
                () -> tournamentService.finishTournament(1L, admin));

        assertTrue(thrown.getMessage().contains("final has not been played"));
        assertEquals(TournamentPhase.ELIMINATION, tournament.getPhase(), "phase must not move");
    }

    @Test
    void anAlreadyClosedSeasonCannotBeClosedTwice() {
        tournament.setPhase(TournamentPhase.CLOSED);
        givenBracket(ana);
        assertTrue(assertThrows(TournamentException.class,
                () -> tournamentService.finishTournament(1L, admin)).getMessage().contains("already closed"));
    }
}
