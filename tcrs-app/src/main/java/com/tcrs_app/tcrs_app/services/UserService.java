package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.payload.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getActiveUsers();
}
