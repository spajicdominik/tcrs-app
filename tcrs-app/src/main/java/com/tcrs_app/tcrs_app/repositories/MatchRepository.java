package com.tcrs_app.tcrs_app.repositories;

import com.tcrs_app.tcrs_app.entities.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
