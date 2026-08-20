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
public class GroupStandingsResponse {

    private Long groupId;
    private String groupName;

    /** How many of this group's players go through to the elimination phase. */
    private Integer qualifiersPerGroup;

    /** True once every match in the group has a result. */
    private Boolean complete;

    /** Fixtures in this group with no result yet; 0 when complete. */
    private Integer matchesRemaining;

    /**
     * False when a tie spans the qualification cut - the last qualifying place and the
     * first non-qualifying place share a position, so the ranking chain has run out and
     * only the admin can separate them.
     */
    private Boolean qualifiersDecided;

    /** Sorted best-first by the ranking chain. */
    private List<StandingRowResponse> rows;
}
