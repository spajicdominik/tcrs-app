package com.tcrs_app.tcrs_app.repositories;

import com.tcrs_app.tcrs_app.entities.Tournament;
import com.tcrs_app.tcrs_app.entities.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentGroupRepository extends JpaRepository<TournamentGroup, Long> {

    List<TournamentGroup> findByTournamentOrderByNameAsc(Tournament tournament);
}
