package com.tcrs_app.tcrs_app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentMatchesResponse {

    private Long tournamentId;

    /** GROUP, ELIMINATION, FINISHED or CLOSED - tells the client how to render the rounds. */
    private String phase;

    /**
     * The round the competition has nominally reached. Informational only: a player may
     * enter the result of ANY of their matches, in any round, whenever it gets played.
     */
    private Integer currentRound;

    /** Every round, always - players need to see and score matches from any of them. */
    private List<TournamentRoundMatches> rounds;
}
