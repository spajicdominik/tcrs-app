package com.tcrs_app.tcrs_app.controllers;

import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.payload.request.CreateReservationRequest;
import com.tcrs_app.tcrs_app.payload.response.ReservationResponse;
import com.tcrs_app.tcrs_app.services.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public List<ReservationResponse> getActiveReservations() {
        return reservationService.getActiveReservations();
    }

    @PostMapping
    public ResponseEntity<?> createReservation(
            @Valid @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found/Unauthorized user");
        }
        ReservationResponse response = reservationService.createReservation(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
