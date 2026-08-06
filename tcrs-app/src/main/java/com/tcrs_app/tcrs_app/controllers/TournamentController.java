package com.tcrs_app.tcrs_app.controllers;

import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.payload.request.CreateTournamentRequest;
import com.tcrs_app.tcrs_app.payload.request.TournamentOptionsRequest;
import com.tcrs_app.tcrs_app.payload.response.TournamentOptionsResponse;
import com.tcrs_app.tcrs_app.payload.response.TournamentResponse;
import com.tcrs_app.tcrs_app.services.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/generate-options")
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
}
