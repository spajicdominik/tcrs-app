package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.payload.request.AuthenticationRequest;
import com.tcrs_app.tcrs_app.payload.request.RegisterRequest;
import com.tcrs_app.tcrs_app.payload.response.AuthenticationResponse;
import com.tcrs_app.tcrs_app.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        return null;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        return null;
    }
}
