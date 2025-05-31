package org.example.restaurantms.Service.UnitTests;

import org.example.restaurantms.entity.Delivery;
import org.example.restaurantms.entity.DeliveryStatus;
import org.example.restaurantms.entity.Order;
import org.example.restaurantms.repository.DeliveryRepository;
import org.example.restaurantms.repository.OrderRepository;
import org.example.restaurantms.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeliveryServiceTest {

    private DeliveryRepository deliveryRepository;
    private OrderRepository orderRepository;
    private DeliveryService deliveryService;

    @BeforeEach
    public void setUp() {
        deliveryRepository = mock(DeliveryRepository.class);
        orderRepository = mock(OrderRepository.class);
        deliveryService = new DeliveryService(deliveryRepository, orderRepository);
    }

    @Test
    @DisplayName("Should return all deliveries")
    public void testGetAllDeliveries() {
        Delivery d1 = new Delivery();
        Delivery d2 = new Delivery();

        when(deliveryRepository.findAll()).thenReturn(Arrays.asList(d1, d2));

        List<Delivery> result = deliveryService.getAllDeliveries();

        assertEquals(2, result.size());
        verify(deliveryRepository).findAll();
    }

    @Test
    @DisplayName("Should create delivery for an order without existing delivery")
    public void testCreateDeliverySuccess() {
        Long orderId = 1L;
        String address = "123 Street";

        Order order = new Order();
        order.setId(orderId);
        order.setDelivery(null);

        Delivery savedDelivery = new Delivery();
        savedDelivery.setId(1L);
        savedDelivery.setAddress(address);
        savedDelivery.setStatus(DeliveryStatus.IN_PROGRESS);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(savedDelivery);

        Delivery result = deliveryService.createDelivery(orderId, address);

        assertNotNull(result);
        assertEquals(address, result.getAddress());
        assertEquals(DeliveryStatus.IN_PROGRESS, result.getStatus());
        verify(orderRepository).findById(orderId);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    @DisplayName("Should throw exception when creating delivery for non-existent order")
    public void testCreateDeliveryOrderNotFound() {
        Long orderId = 99L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                deliveryService.createDelivery(orderId, "Nowhere"));

        assertTrue(exception.getMessage().contains("Zamówienie o ID " + orderId + " nie istnieje"));
        verify(orderRepository).findById(orderId);
        verify(deliveryRepository, never()).save(any());
    }

}
