package com.tcrs_app.tcrs_app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** One valid way to split the selected players into groups. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentOption {

    private Integer numberOfGroups;

    /** Size of each group, largest first, e.g. [6, 6, 6, 5] for 23 players in 4 groups. */
    private List<Integer> groupSizes;

    /** False when the players cannot be split evenly (some groups get one extra player). */
    private Boolean evenGroups;

    /** Qualifiers-per-group values that produce a power-of-two bracket, e.g. [1, 2, 4]. */
    private List<Integer> qualifiersPerGroupOptions;
}
