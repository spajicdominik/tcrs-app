package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.Reservation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{
    @Override
    public List<Reservation> getAllReservations() {
        return List.of();
    }
}
