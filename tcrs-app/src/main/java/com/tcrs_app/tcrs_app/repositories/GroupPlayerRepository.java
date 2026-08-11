package com.tcrs_app.tcrs_app.repositories;

import com.tcrs_app.tcrs_app.entities.GroupPlayer;
import com.tcrs_app.tcrs_app.entities.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupPlayerRepository extends JpaRepository<GroupPlayer, Long> {

    List<GroupPlayer> findByGroup(TournamentGroup group);
}
