package com.tcrs_app.tcrs_app.repositories;

import com.tcrs_app.tcrs_app.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByTimeStartBetween(OffsetDateTime from, OffsetDateTime to);

    // two intervals overlap when each starts before the other ends;
    // canceled reservations free up their slot
    @Query("""
            SELECT COUNT(r) > 0 FROM Reservation r
            WHERE r.canceled = false
              AND r.timeStart < :end
              AND r.timeEnd > :start
            """)
    boolean existsOverlapping(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);
}
