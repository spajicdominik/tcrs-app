package com.tcrs_app.tcrs_app.controllers;

import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.payload.request.CreateReservationRequest;
import com.tcrs_app.tcrs_app.payload.response.ReservationResponse;
import com.tcrs_app.tcrs_app.payload.response.UserResponse;
import com.tcrs_app.tcrs_app.services.ReservationService;
import com.tcrs_app.tcrs_app.services.UserService;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getActiveUsers() {
        return userService.getActiveUsers();
    }

}
