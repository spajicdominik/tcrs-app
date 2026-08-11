package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.Match;
import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.enums.AppUserRole;
import com.tcrs_app.tcrs_app.exception.TournamentException;
import com.tcrs_app.tcrs_app.payload.request.MatchInsertRequest;
import com.tcrs_app.tcrs_app.payload.response.MatchInsertResponse;
import com.tcrs_app.tcrs_app.repositories.MatchRepository;
import com.tcrs_app.tcrs_app.repositories.TournamentGroupRepository;
import com.tcrs_app.tcrs_app.repositories.TournamentRepository;
import com.tcrs_app.tcrs_app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TournamentGroupRepository tournamentGroupRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    private User admin;
    private User player1;
    private User player2;
    private Match match;

    @BeforeEach
    void setUp() {
        admin = User.builder().id(1L).firstName("Admin").lastName("User").role(AppUserRole.ADMIN).build();
        player1 = User.builder().id(2L).firstName("Player").lastName("1").role(AppUserRole.PLAYER).build();
        player2 = User.builder().id(3L).firstName("Player").lastName("2").role(AppUserRole.PLAYER).build();

        match = new Match();
        match.setId(10L);
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setRoundNumber(1);
    }

    @Test
    void insertTournamentMatchResultSuccessful() {
        MatchInsertRequest request = MatchInsertRequest.builder()
                .matchId(10L)
                .player1Games(9)
                .player2Games(5)
                .build();

        when(matchRepository.findById(10L)).thenReturn(Optional.of(match));

        MatchInsertResponse response = tournamentService.insertTournamentMatchResult(request, admin);

        assertNotNull(response);
        assertEquals(9, match.getPlayer1Games());
        assertEquals(5, match.getPlayer2Games());
        assertEquals(player1, match.getWinner());
        assertEquals(admin, match.getScoreEnteredBy());
        verify(matchRepository).save(match);
    }

    @Test
    void insertTournamentMatchResultTightWinSuccessful() {
        MatchInsertRequest request = MatchInsertRequest.builder()
                .matchId(10L)
                .player1Games(8)
                .player2Games(9)
                .build();

        when(matchRepository.findById(10L)).thenReturn(Optional.of(match));

        MatchInsertResponse response = tournamentService.insertTournamentMatchResult(request, admin);

        assertEquals(8, match.getPlayer1Games());
        assertEquals(9, match.getPlayer2Games());
        assertEquals(player2, match.getWinner());
    }

    @Test
    void insertTournamentMatchResultFailsOnTie() {
        MatchInsertRequest request = MatchInsertRequest.builder()
                .matchId(10L)
                .player1Games(9)
                .player2Games(9)
                .build();

        when(matchRepository.findById(10L)).thenReturn(Optional.of(match));

        TournamentException exception = assertThrows(TournamentException.class, () -> 
            tournamentService.insertTournamentMatchResult(request, admin)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Matches cannot end in a tie.", exception.getMessage());
    }

    @Test
    void insertTournamentMatchResultFailsOnInvalidScore() {
        MatchInsertRequest request = MatchInsertRequest.builder()
                .matchId(10L)
                .player1Games(8)
                .player2Games(7)
                .build();

        when(matchRepository.findById(10L)).thenReturn(Optional.of(match));

        TournamentException exception = assertThrows(TournamentException.class, () -> 
            tournamentService.insertTournamentMatchResult(request, admin)
        );
        assertEquals("One player must have 9 games.", exception.getMessage());
    }

    @Test
    void insertTournamentMatchResultFailsOnForbiddenUser() {
        User otherPlayer = User.builder().id(4L).role(AppUserRole.PLAYER).build();
        MatchInsertRequest request = MatchInsertRequest.builder().matchId(10L).build();

        when(matchRepository.findById(10L)).thenReturn(Optional.of(match));

        TournamentException exception = assertThrows(TournamentException.class, () -> 
            tournamentService.insertTournamentMatchResult(request, otherPlayer)
        );
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }
}
