package com.tcrs_app.tcrs_app.controllers;

import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.payload.request.CreateTournamentRequest;
import com.tcrs_app.tcrs_app.payload.request.MatchInsertRequest;
import com.tcrs_app.tcrs_app.payload.request.TournamentOptionsRequest;
import com.tcrs_app.tcrs_app.payload.response.*;
import com.tcrs_app.tcrs_app.services.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTournament(
            @Valid @RequestBody CreateTournamentRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found/Unauthorized user");
        }
        TournamentResponse response = tournamentService.createTournament(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/generate-options")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateTournamentOptions(
            @Valid @RequestBody TournamentOptionsRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found/Unauthorized user");
        }
        TournamentOptionsResponse response = tournamentService.generateTournamentOptions(request, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{id}/generate-matches")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateTournamentMatches(
            @Valid @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found/Unauthorized user");
        }
        TournamentMatchesResponse response = tournamentService.generateTournamentMatches(id, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * The season currently running, if any. Drives whether a new tournament may be
     * created and whether the current one can be closed.
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveTournament(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found/Unauthorized user");
        }
        return ResponseEntity.status(HttpStatus.OK).body(tournamentService.getActiveTournament(currentUser));
    }

    /** Closes a completed season, which frees the club to create the next one. */
    @PostMapping("/{id}/finish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> finishTournament(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found/Unauthorized user");
        }
        return ResponseEntity.status(HttpStatus.OK).body(tournamentService.finishTournament(id, currentUser));
    }

    @GetMapping("/{id}/standings")
    public ResponseEntity<?> getCurrentTournamentStandings(
            @Valid @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found/Unauthorized user");
        }
        TournamentStandingsResponse response = tournamentService.getCurrentTournamentStandings(id, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}/matches")
    public ResponseEntity<?> getTournamentMatchesByRounds(
            @Valid @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found/Unauthorized user");
        }
        List<MatchRoundsResponse> roundMatches = tournamentService.getTorunamentMatchesByRounds(id, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(roundMatches);
    }

    @PutMapping("/{id}/matches")
    public ResponseEntity<?> insertTournamentMatchResult(
            @Valid @RequestBody MatchInsertRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found/Unauthorized user");
        }
        MatchInsertResponse response = tournamentService.insertTournamentMatchResult(request, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
