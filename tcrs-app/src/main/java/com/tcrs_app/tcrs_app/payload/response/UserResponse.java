package com.tcrs_app.tcrs_app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;

    // only populated for the current user (/auth/me); left null in listings
    // so one player's email/role is not exposed to every other player
    private String email;
    private String role;
    private String phoneNumber;
}
