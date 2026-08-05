package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.enums.AppUserStatus;
import com.tcrs_app.tcrs_app.payload.response.UserResponse;
import com.tcrs_app.tcrs_app.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public List<UserResponse> getActiveUsers() {
        return userRepository.findByStatus(AppUserStatus.ACTIVE)
                .stream().map(this::toResponse).toList();
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
