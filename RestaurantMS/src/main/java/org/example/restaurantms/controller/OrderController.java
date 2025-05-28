package org.example.restaurantms.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.restaurantms.entity.Order;
import org.example.restaurantms.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {this.orderService = orderService;}

    @Operation(summary = "Create a new order", description = "Creates a new order with order items and optional delivery")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody JsonNode requestBody) {
        Order createdOrder = orderService.createOrder(requestBody);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all orders", description = "Returns a list of all orders")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Delete an Order by ID", description = "Deletes an existing Order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "ID of the Order to delete", required = true)
            @PathVariable Long id) {
        boolean deleted = orderService.deleteOrderById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get an order by ID", description = "Returns a single order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable Long id) {

        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}
