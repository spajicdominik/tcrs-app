package com.tcrs_app.tcrs_app.repositories;

import com.tcrs_app.tcrs_app.entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
}
