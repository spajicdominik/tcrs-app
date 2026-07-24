package com.tcrs_app.tcrs_app.controllers;

import com.tcrs_app.tcrs_app.payload.response.EnableUserResponse;
import com.tcrs_app.tcrs_app.services.EnableUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EnableUserService enableUserService;

    @PutMapping("/users/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnableUserResponse> enableUser(@PathVariable Long id) {
        return ResponseEntity.ok(enableUserService.enableUser(id));
    }
}
