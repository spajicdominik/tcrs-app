package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.Reservation;
import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.exception.ReservationException;
import com.tcrs_app.tcrs_app.payload.request.CreateReservationRequest;
import com.tcrs_app.tcrs_app.payload.response.ReservationResponse;
import com.tcrs_app.tcrs_app.repositories.ReservationRepository;
import com.tcrs_app.tcrs_app.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    // court rules: fixed 90-minute slots, court open 07:00–20:30,
    // so the latest a reservation may start is 19:00
    private static final int DURATION_MINUTES = 90;
    private static final LocalTime OPENING_TIME = LocalTime.of(7, 0);
    private static final LocalTime LATEST_START = LocalTime.of(19, 0);
    private static final int MAX_DAYS_AHEAD = 7;

    @Override
    public List<ReservationResponse> getActiveReservations() {
        OffsetDateTime now = OffsetDateTime.now();
        return reservationRepository.findByTimeStartBetween(now, now.plusDays(MAX_DAYS_AHEAD))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ReservationResponse createReservation(CreateReservationRequest request, User currentUser) {
        OffsetDateTime start = request.getTimeStart();
        OffsetDateTime now = OffsetDateTime.now();

        if (start.getMinute() % 30 != 0 || start.getSecond() != 0 || start.getNano() != 0) {
            throw new ReservationException("Reservations must start on the hour or half hour.", HttpStatus.BAD_REQUEST);
        }

        if (start.isBefore(now)) {
            throw new ReservationException("Cannot book a reservation in the past.", HttpStatus.BAD_REQUEST);
        }

        if (start.isAfter(now.plusDays(MAX_DAYS_AHEAD))) {
            throw new ReservationException("Reservations can be booked at most one week in advance.", HttpStatus.BAD_REQUEST);
        }

        LocalTime startTime = start.toLocalTime();
        if (startTime.isBefore(OPENING_TIME) || startTime.isAfter(LATEST_START)) {
            throw new ReservationException("Reservations must start between 07:00 and 19:00.", HttpStatus.BAD_REQUEST);
        }

        OffsetDateTime end = start.plusMinutes(DURATION_MINUTES);

        if (reservationRepository.existsOverlapping(start, end)) {
            throw new ReservationException("The selected time slot is already booked.", HttpStatus.CONFLICT);
        }

        User partner = null;
        if (request.getPartnerId() != null) {
            if (request.getPartnerId().equals(currentUser.getId())) {
                throw new ReservationException("You cannot add yourself as a partner.", HttpStatus.BAD_REQUEST);
            }
            partner = userRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> new ReservationException("Partner not found.", HttpStatus.NOT_FOUND));
        }

        Reservation reservation = new Reservation();
        reservation.setTimeStart(start);
        reservation.setTimeEnd(end);
        reservation.setCanceled(false);
        reservation.setMatchType(request.getMatchType());
        reservation.setMainPlayer(currentUser);
        reservation.setPartner(partner);

        Reservation saved = reservationRepository.save(reservation);
        return toResponse(saved);
    }

    private ReservationResponse toResponse(Reservation reservation) {
        User main = reservation.getMainPlayer();
        User partner = reservation.getPartner();
        return ReservationResponse.builder()
                .id(reservation.getId())
                .timeStart(reservation.getTimeStart())
                .timeEnd(reservation.getTimeEnd())
                .matchType(reservation.getMatchType().name())
                .canceled(reservation.isCanceled())
                .mainPlayerId(main.getId())
                .mainPlayerName(main.getFirstName() + " " + main.getLastName())
                .partnerId(partner != null ? partner.getId() : null)
                .partnerName(partner != null ? partner.getFirstName() + " " + partner.getLastName() : null)
                .build();
    }
}
