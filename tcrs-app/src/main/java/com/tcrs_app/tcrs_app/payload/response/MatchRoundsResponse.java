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
public class MatchRoundsResponse {
    private Integer roundNumber;

    private String roundName;

    private List<CurrentRoundGroupMatches> matches;
}
