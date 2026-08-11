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

    /**
     * The tournament this match belongs to. Always set - it is the only link for
     * elimination matches, which have no group.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    // null for elimination matches
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private TournamentGroup group;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    // nullable: an elimination slot exists before the match feeding it has been decided
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id")
    private User player1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id")
    private User player2;

    // null until the match is played
    @Column(name = "player1_games")
    private Integer player1Games;

    @Column(name = "player2_games")
    private Integer player2Games;

    // null while unresolved
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    // null until a score is entered
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_entered_by")
    private User scoreEnteredBy;

    // self-reference: the match the winner advances to; null for group matches and the final
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_match_id")
    private Match nextMatch;
}
