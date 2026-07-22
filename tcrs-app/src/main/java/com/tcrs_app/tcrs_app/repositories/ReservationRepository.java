package com.tcrs_app.tcrs_app.repositories;

import com.tcrs_app.tcrs_app.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
