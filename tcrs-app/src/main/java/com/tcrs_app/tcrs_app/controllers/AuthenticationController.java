package com.tcrs_app.tcrs_app.controllers;

import com.tcrs_app.tcrs_app.services.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirements()
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

}
