package org.example.restaurantms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.restaurantms.entity.Reservation;
import org.example.restaurantms.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.restaurantms.DTO.ReservationDTO;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "Get all reservations", description = "Returns a list of all reservations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    })
    @GetMapping("/get")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @Operation(summary = "Create a reservation",
            description = "Creates a reservation for a table for a specific user and time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation created successfully"),
            @ApiResponse(responseCode = "404", description = "User or table not found")
    })
    @PostMapping("/post")
    public ResponseEntity<ReservationDTO> createReservation(
            @Parameter(description = "User ID to which the reservation will be set")
            @RequestParam Long userId,
            @Parameter(description = "ID of the table to reserve")
            @RequestParam Long tableId,
            @Parameter(description = "Date and time of reservation in format: (\"yyyy-MM-dd HH:mm:ss\")")
            @RequestParam String startTime
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsedStartTime = LocalDateTime.parse(startTime, formatter);

        Reservation createdReservation = reservationService.createReservation(userId, tableId, parsedStartTime);

        ReservationDTO dto = new ReservationDTO();
        dto.setId(createdReservation.getId());
        dto.setStartTime(createdReservation.getStartTime().toString());
        dto.setEndTime(createdReservation.getEndTime().toString());
        dto.setUserId(createdReservation.getUser().getId());
        dto.setTableId(createdReservation.getR_table().getId());

        return ResponseEntity.status(201).body(dto);
    }

    @Operation(summary = "Delete a reservation", description = "Cancels and deletes an existing reservation by its ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Reservation deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReservation(
            @Parameter(description = "Id of reservation to cancel")
            @PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Update reservation",
            description = "Allows changing the reservation date and/or table")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservation updated successfully"),
            @ApiResponse(responseCode = "404", description = "Reservation or table not found")
    })
    @PatchMapping("/patch/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @Parameter(description = "ID of reservation to update")
            @PathVariable Long id,
            @Parameter(description = "ID of desired table")
            @RequestParam(required = false) Long tableId,
            @Parameter(description = "New reservation date in format: (yyyy-MM-dd HH:mm:ss)")
            @RequestParam(required = false) String startTime // "yyyy-MM-dd HH:mm:ss"
    ) {
        LocalDateTime parsedStartTime = null;
        if (startTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            parsedStartTime = LocalDateTime.parse(startTime, formatter);
        }

        Reservation updated = reservationService.updateReservation(id, tableId, parsedStartTime);
        return ResponseEntity.ok(updated);
    }
}
