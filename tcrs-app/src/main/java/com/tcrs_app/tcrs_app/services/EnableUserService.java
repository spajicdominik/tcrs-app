package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.payload.request.EnableUserRequest;
import com.tcrs_app.tcrs_app.payload.response.EnableUserResponse;

public interface EnableUserService {
    EnableUserResponse enableUser(EnableUserRequest request);
}
