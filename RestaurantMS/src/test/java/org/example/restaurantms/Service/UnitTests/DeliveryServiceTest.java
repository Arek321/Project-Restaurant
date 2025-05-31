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


}
