package com.tcrs_app.tcrs_app.repositories;

import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.enums.AppUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // login: look a user up by their username
    Optional<User> findByUsername(String username);

    // registration validation: is this username / email already taken?
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // admin: list everyone awaiting approval (or any status)
    List<User> findByStatus(AppUserStatus status);
}
