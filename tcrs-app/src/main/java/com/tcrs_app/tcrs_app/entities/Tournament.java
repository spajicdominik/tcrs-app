package com.tcrs_app.tcrs_app.entities;

import com.tcrs_app.tcrs_app.enums.TournamentPhase;
import jakarta.persistence.*;
import lombok.*;

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
}
