package com.tcrs_app.tcrs_app.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTournamentRequest {

    @NotNull(message = "name is required")
    private String name;

    @NotNull(message = "number of groups is required")
    private Integer numberOfGroups;

    @NotNull(message = "number of players per group is required")
    private Integer playersPerGroup;

    @NotNull(message = "number of qualifiers per group is required")
    private Integer qualifiersPerGroup;
}
