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

    /** Sorted best-first by the ranking chain. */
    private List<StandingRowResponse> rows;
}
