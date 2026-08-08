package com.tcrs_app.tcrs_app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * One round of a tournament.
 *
 * Matches are kept flat rather than nested per group: each MatchResponse carries its
 * own groupId/groupName, so the same shape serves the group phase (client groups by
 * groupId) and the elimination phase (no groups at all).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentRoundMatches {

    private Integer roundNumber;

    /** Label for elimination rounds ("Četvrtfinale", "Polufinale", "Finale"); null in the group phase. */
    private String roundName;

    private List<MatchResponse> matches;
}
