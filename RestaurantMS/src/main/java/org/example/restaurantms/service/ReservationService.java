package org.example.restaurantms.service;

import lombok.RequiredArgsConstructor;
import org.example.restaurantms.entity.R_Table;
import org.example.restaurantms.entity.Reservation;
import org.example.restaurantms.entity.User;
import org.example.restaurantms.repository.R_TableRepository;
import org.example.restaurantms.repository.ReservationRepository;
import org.example.restaurantms.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final R_TableRepository tableRepository;
    private static final LocalTime OPENING_TIME = LocalTime.of(10, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(20, 0);

    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository, R_TableRepository tableRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.tableRepository = tableRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation createReservation(Long userId, Long tableId, LocalDateTime startTime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        R_Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Table not found"));

        LocalDateTime endTime = startTime.plusHours(2);
        LocalTime time = startTime.toLocalTime();

        //Walidacja godzin otwarcia
        if (time.isBefore(LocalTime.of(10, 0)) || time.isAfter(LocalTime.of(20, 0))) {
            throw new RuntimeException("Restauracja jest czynna od 10:00 do 22:00. Rezerwacje mogą zaczynać się między 10:00 a 20:00.");
        }

        //Walidacja kolizji z innymi rezerwacjami
        boolean conflict = reservationRepository.isTableReservedInTimeRange(
                tableId, startTime, endTime, -1L  // -1 bo to nowa rezerwacja, nie ma ID
        );

        if (conflict) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Stolik jest już zarezerwowany w podanym czasie (±2h)."
            );
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setR_table(table);
        reservation.setStartTime(startTime);
        reservation.setEndTime(startTime.plusHours(2));

        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found with id: " + id));
        reservationRepository.delete(reservation);
    }

    public Reservation updateReservation(Long reservationId, Long tableId, LocalDateTime newStartTime) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        // aktualne wartosci, jesli brak nowych
        LocalDateTime effectiveStartTime = newStartTime != null ? newStartTime : reservation.getStartTime();
        Long effectiveTableId = tableId != null ? tableId : reservation.getR_table().getId();

        // czy rezerwacja koliduje
        LocalDateTime effectiveEndTime = effectiveStartTime.plusHours(2);

        //walidacja godzin otwarcia
        LocalTime time = effectiveStartTime.toLocalTime();
        if (time.isBefore(LocalTime.of(10, 0)) || time.isAfter(LocalTime.of(20, 0))) {
            throw new RuntimeException("Restauracja jest czynna od 10:00 do 22:00. Rezerwacja musi się w tym mieścić pomiędzy 10:00 a 20:00");
        }

        boolean conflict = reservationRepository.isTableReservedInTimeRange(
                effectiveTableId,
                effectiveStartTime,
                effectiveEndTime,
                reservationId
        );

        if (conflict) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Stolik jest już zarezerwowany w podanym czasie (±2h)."
            );
        }

        // jesli chce nowy stolik to ustawiam go
        if (tableId != null) {
            R_Table newTable = tableRepository.findById(tableId)
                    .orElseThrow(() -> new RuntimeException("Table not found with id: " + tableId));
            reservation.setR_table(newTable);
        }

        // jesli nowy czas - to ustawiam czas
        if (newStartTime != null) {
            reservation.setStartTime(effectiveStartTime);
            reservation.setEndTime(effectiveEndTime);
        }

        return reservationRepository.save(reservation);
    }
}
