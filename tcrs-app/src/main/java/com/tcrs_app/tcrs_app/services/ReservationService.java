package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.Reservation;
import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.payload.request.CreateReservationRequest;
import com.tcrs_app.tcrs_app.payload.response.ReservationResponse;

import java.util.List;

public interface ReservationService {
    List<Reservation> getActiveReservations();

    ReservationResponse createReservation(CreateReservationRequest request, User currentUser);
}
