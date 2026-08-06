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
public class TournamentOptionsResponse {

    private Integer numberOfPlayers;

    /** Empty when no valid layout exists for this player count. */
    private List<TournamentOption> options;
}
