package com.tcrs_app.tcrs_app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long id;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private String matchType;
    private boolean canceled;

    private Long mainPlayerId;
    private String mainPlayerName;

    private Long partnerId;
    private String partnerName;
}
