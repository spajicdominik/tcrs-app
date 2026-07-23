package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.payload.request.EnableUserRequest;
import com.tcrs_app.tcrs_app.payload.response.EnableUserResponse;
import com.tcrs_app.tcrs_app.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class EnableUserServiceImpl implements EnableUserService{

    private final UserRepository userRepository;

    @Override
    public EnableUserResponse enableUser(EnableUserRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent()) {
            userRepository.enableUser(request.getEmail());
            return new EnableUserResponse("User enabled successfully");
        }
        return new EnableUserResponse("User not found");
    }
}
