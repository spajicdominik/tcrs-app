package com.tcrs_app.tcrs_app.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchInsertRequest {
    private Long matchId;
    private Long player1Id;
    private Long player2Id;
    private Integer player1Games;
    private Integer player2Games;
    private Long scoreEnteredBy;
}
