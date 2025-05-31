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


}
