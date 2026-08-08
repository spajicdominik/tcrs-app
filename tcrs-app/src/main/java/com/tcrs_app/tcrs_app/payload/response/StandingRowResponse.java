package com.tcrs_app.tcrs_app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One player's line in a group table. Everything here is COMPUTED from the played
 * matches - nothing is stored, so the table stays correct no matter what order the
 * results come in.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandingRowResponse {

    /** 1-based place in the group. Players who are still level share a position. */
    private Integer position;

    private Long playerId;
    private String playerName;

    /**
     * Matches this player has actually played. Players progress at different speeds -
     * someone may have played three matches while an opponent has played none - so this
     * is shown next to the points to make the table readable.
     */
    private Integer played;

    /** Total matches scheduled for this player in the group, played or not. */
    private Integer scheduled;

    /** One point per win, no points for a loss. */
    private Integer points;

    private Integer gamesWon;
    private Integer gamesLost;

    /** gamesWon - gamesLost; the first tie-break. */
    private Integer gameDifference;
}
