package com.tcrs_app.tcrs_app.payload.request;

import com.tcrs_app.tcrs_app.enums.MatchType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {

    @NotNull(message = "start time is required")
    private OffsetDateTime timeStart;

    @NotNull(message = "match type is required")
    private MatchType matchType;

    // optional: added immediately, no invitation
    private Long partnerId;
}
