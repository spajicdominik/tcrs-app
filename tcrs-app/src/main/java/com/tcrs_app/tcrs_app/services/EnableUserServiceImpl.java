package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.enums.AppUserStatus;
import com.tcrs_app.tcrs_app.payload.response.EnableUserResponse;
import com.tcrs_app.tcrs_app.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class EnableUserServiceImpl implements EnableUserService {

    private final UserRepository userRepository;

    @Override
    public EnableUserResponse enableUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (user.getStatus() != AppUserStatus.PENDING) {
            throw new IllegalArgumentException("User is not in PENDING status.");
        }

        user.setStatus(AppUserStatus.ACTIVE);
        return new EnableUserResponse("User enabled successfully.");
    }
}
