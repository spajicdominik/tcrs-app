package com.tcrs_app.tcrs_app.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "match")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // null for elimination matches
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private TournamentGroup group;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player1_id", nullable = false)
    private AppUser player1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player2_id", nullable = false)
    private AppUser player2;

    // null until the match is played
    @Column(name = "player1_games")
    private Integer player1Games;

    @Column(name = "player2_games")
    private Integer player2Games;

    // null while unresolved
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private AppUser winner;

    // null until a score is entered
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_entered_by")
    private AppUser scoreEnteredBy;

    // self-reference: the match the winner advances to; null for group matches and the final
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_match_id")
    private Match nextMatch;
}
