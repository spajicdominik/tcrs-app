package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.payload.request.AuthenticationRequest;
import com.tcrs_app.tcrs_app.payload.request.RegisterRequest;
import com.tcrs_app.tcrs_app.payload.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
