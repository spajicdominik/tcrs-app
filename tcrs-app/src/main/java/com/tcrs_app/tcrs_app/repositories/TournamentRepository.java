package com.tcrs_app.tcrs_app.repositories;

import com.tcrs_app.tcrs_app.entities.Tournament;
import com.tcrs_app.tcrs_app.enums.TournamentPhase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByPhaseNot(TournamentPhase phase);
}
