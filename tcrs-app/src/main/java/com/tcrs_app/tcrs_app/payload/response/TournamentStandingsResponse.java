package com.tcrs_app.tcrs_app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Every group table plus the one question the admin screen needs answered: can the
 * elimination bracket be drawn yet?
 *
 * That verdict is computed here rather than in the client because the endpoint which
 * actually creates the bracket has to enforce the same rule - having it in one place
 * keeps the button and the guard from disagreeing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentStandingsResponse {

    private Long tournamentId;

    private String phase;

    /**
     * True when every group is finished AND its qualifiers are unambiguous. The
     * per-group flags say which of the two is missing.
     */
    private Boolean readyForElimination;

    private List<GroupStandingsResponse> groups;
}
