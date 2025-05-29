package org.example.restaurantms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.restaurantms.entity.Delivery;
import org.example.restaurantms.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequestMapping("/api/deliveries")
@Tag(name = "Delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Operation(summary = "Get all deliveries", description = "Returns a list of created deliveries")
    @ApiResponse(responseCode = "200", description = "List returned successfully")
    @GetMapping("/get")
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        List<Delivery> deliveries = deliveryService.getAllDeliveries();
        return ResponseEntity.ok(deliveries);
    }

    @Operation(summary = "Create a delivery for an order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery created successfully"),
            @ApiResponse(responseCode = "400", description = "Error during validation of delivery data or order")
    })
    @PostMapping("/post")
    public ResponseEntity<Delivery> createDelivery(
            @RequestParam Long orderId,
            @RequestParam String address
    ) {
        Delivery delivery = deliveryService.createDelivery(orderId, address);
        return ResponseEntity.ok(delivery);
    }

    @Operation(summary = "Delete delivery", description = "Deletes a delivery by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDeliveryById(id);
        return ResponseEntity.ok("Delivery deleted successfully");
    }
}
