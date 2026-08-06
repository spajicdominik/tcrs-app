package com.tcrs_app.tcrs_app.payload.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentOptionsRequest {

    @NotEmpty(message = "players are required")
    private List<Long> playerIds;
}
