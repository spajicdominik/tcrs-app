package com.tcrs_app.tcrs_app.entities;

import com.tcrs_app.tcrs_app.enums.EliminationFormat;
import com.tcrs_app.tcrs_app.enums.TournamentPhase;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tournament")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "phase", nullable = false, length = 20)
    private TournamentPhase phase;

    @Column(name = "qualifiers_per_group", nullable = false)
    private Integer qualifiersPerGroup;

    /** Which shape the knockout stage takes; chosen when the tournament is created. */
    @Enumerated(EnumType.STRING)
    @Column(name = "elimination_format", nullable = false, length = 20)
    private EliminationFormat eliminationFormat;

    /**
     * The round the competition has nominally reached. Informational: players may enter
     * the result of any of their matches, from any round, whenever it gets played.
     */
    @Column(name = "current_round", nullable = false)
    private Integer currentRound;
}
