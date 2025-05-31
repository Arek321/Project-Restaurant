package org.example.restaurantms.Service.UnitTests;

import org.example.restaurantms.entity.R_Table;
import org.example.restaurantms.entity.Reservation;
import org.example.restaurantms.entity.User;
import org.example.restaurantms.repository.R_TableRepository;
import org.example.restaurantms.repository.ReservationRepository;
import org.example.restaurantms.repository.UserRepository;
import org.example.restaurantms.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {

    private ReservationRepository reservationRepository;
    private UserRepository userRepository;
    private R_TableRepository tableRepository;
    private ReservationService reservationService;

    @BeforeEach
    public void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        userRepository = mock(UserRepository.class);
        tableRepository = mock(R_TableRepository.class);
        reservationService = new ReservationService(reservationRepository, userRepository, tableRepository);
    }

    @Test
    @DisplayName("Should return all reservations")
    public void testGetAllReservations() {
        Reservation r1 = new Reservation();
        Reservation r2 = new Reservation();

        when(reservationRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<Reservation> result = reservationService.getAllReservations();

        assertEquals(2, result.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should create a new reservation successfully")
    public void testCreateReservationSuccess() {
        Long userId = 1L;
        Long tableId = 2L;
        LocalDateTime startTime = LocalDateTime.of(2025, 6, 1, 12, 0);

        User user = new User();
        user.setId(userId);

        R_Table table = new R_Table();
        table.setId(tableId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));
        when(reservationRepository.isTableReservedInTimeRange(tableId, startTime, startTime.plusHours(2), -1L)).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation result = reservationService.createReservation(userId, tableId, startTime);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(table, result.getR_table());
        assertEquals(startTime.plusHours(2), result.getEndTime());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception if reservation time is outside opening hours")
    public void testCreateReservationInvalidTime() {
        Long userId = 1L;
        Long tableId = 2L;
        LocalDateTime startTime = LocalDateTime.of(2025, 6, 1, 8, 0); // przed 10:00

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(tableRepository.findById(tableId)).thenReturn(Optional.of(new R_Table()));

        Exception exception = assertThrows(RuntimeException.class, () ->
                reservationService.createReservation(userId, tableId, startTime));

        assertTrue(exception.getMessage().contains("Restauracja jest czynna"));
    }

    @Test
    @DisplayName("Should throw exception if reservation conflicts with another")
    public void testCreateReservationConflict() {
        Long userId = 1L;
        Long tableId = 2L;
        LocalDateTime startTime = LocalDateTime.of(2025, 6, 1, 12, 0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(tableRepository.findById(tableId)).thenReturn(Optional.of(new R_Table()));
        when(reservationRepository.isTableReservedInTimeRange(tableId, startTime, startTime.plusHours(2), -1L)).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () ->
                reservationService.createReservation(userId, tableId, startTime));

        assertTrue(exception.getMessage().contains("Stolik jest już zarezerwowany"));
    }

    @Test
    @DisplayName("Should delete reservation if it exists")
    public void testDeleteReservationSuccess() {
        Long reservationId = 1L;
        Reservation reservation = new Reservation();
        reservation.setId(reservationId);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        reservationService.deleteReservation(reservationId);

        verify(reservationRepository).delete(reservation);
    }

}
