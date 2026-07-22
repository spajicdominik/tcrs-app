package com.tcrs_app.tcrs_app.repositories;

import com.tcrs_app.tcrs_app.entities.AppUser;
import com.tcrs_app.tcrs_app.enums.AppUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // login: look a user up by their username
    Optional<AppUser> findByUsername(String username);

    // registration validation: is this username / email already taken?
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // admin: list everyone awaiting approval (or any status)
    List<AppUser> findByStatus(AppUserStatus status);
}
