package com.tcrs_app.tcrs_app.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);
    String generateToken(UserDetails userDetails);
    boolean isTokenValid(String token, UserDetails userDetails);
    ResponseCookie generateJwtCookie(String jwt);
    String getJwtFromCookies(HttpServletRequest request);
    ResponseCookie getCleanJwtCookie();
}
