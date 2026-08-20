package com.tcrs_app.tcrs_app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Whether a season is currently running, and what can be done with it.
 *
 * Always returned as an object, even when nothing is active - a null body would be an
 * empty string to the axios client, which is easy to mishandle. `active` is the flag
 * to branch on; every other field is null when it is false.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveTournamentResponse {

    /** False when every tournament is closed, i.e. a new one may be created. */
    private Boolean active;

    private Long id;

    private String name;

    private String phase;

    /**
     * True once the final has been played, so the season can be closed. Closing is what
     * frees the club to create the next tournament.
     */
    private Boolean canFinish;
}
