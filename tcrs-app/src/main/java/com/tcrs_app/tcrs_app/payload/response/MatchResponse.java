package com.tcrs_app.tcrs_app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single match, self-describing so it can be returned on its own (for example in
 * "my unresolved matches") as well as nested inside a round.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {

    private Long id;

    // ---- context -------------------------------------------------------------
    private Integer roundNumber;

    /** Null for elimination matches: those do not belong to a group. */
    private Long groupId;
    private String groupName;

    /** Null for group matches and for the final; otherwise the match the winner advances to. */
    private Long nextMatchId;

    // ---- players -------------------------------------------------------------
    // null in an elimination slot whose feeding match has not been decided yet
    private Long player1Id;
    private String player1Name;
    private Long player2Id;
    private String player2Name;

    // ---- result --------------------------------------------------------------
    /** Null until the match has been played. */
    private Integer player1Games;
    private Integer player2Games;

    /** Null while unresolved; read by the standings and by bracket advancement. */
    private Long winnerId;

    /**
     * Null until a score is entered. The UI uses it to choose between "enter result"
     * and "edit result" - only this player, or an admin, may change the score.
     */
    private Long scoreEnteredById;

    /**
     * Whether the requesting user may submit or change this result right now, computed
     * server-side so the permission rule lives in one place. True when they are one of
     * the two players and the match is either unplayed or was scored by them, or when
     * they are an admin. Round number does not matter: a match can be played late and
     * scored at any time.
     */
    private Boolean canEnterScore;
}
