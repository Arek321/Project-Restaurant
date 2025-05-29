package org.example.restaurantms.repository;

import org.example.restaurantms.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
    SELECT COUNT(r) > 0
    FROM Reservation r
    WHERE r.r_table.id = :tableId
      AND r.id <> :excludedReservationId
      AND (
           :targetStart < r.endTime AND :targetEnd > r.startTime
      )
    """)
    boolean isTableReservedInTimeRange(@Param("tableId") Long tableId,
                                       @Param("targetStart") LocalDateTime targetStart,
                                       @Param("targetEnd") LocalDateTime targetEnd,
                                       @Param("excludedReservationId") Long excludedReservationId);
}
