package org.example.restaurantms.Service.UnitTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.example.restaurantms.entity.*;
import org.example.restaurantms.repository.*;
import org.example.restaurantms.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private MenuItemRepository menuItemRepository;
    private OrderItemRepository orderItemRepository;
    private DeliveryRepository deliveryRepository;
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        orderRepository = mock(OrderRepository.class);
        userRepository = mock(UserRepository.class);
        menuItemRepository = mock(MenuItemRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        deliveryRepository = mock(DeliveryRepository.class);

        orderService = new OrderService(orderRepository, userRepository, menuItemRepository, orderItemRepository, deliveryRepository);
    }

    @Test
    @DisplayName("Should create a new order with delivery and items")
    public void testCreateOrderWithDelivery() {
        // Przygotowanie danych
        Long userId = 1L;
        Long menuItemId = 10L;

        ObjectNode request = objectMapper.createObjectNode();
        request.put("userId", userId);
        request.put("deliveryType", "DELIVERY");
        request.put("deliveryAddress", "Test Street 123");

        ArrayNode itemsArray = objectMapper.createArrayNode();
        ObjectNode itemNode = objectMapper.createObjectNode();
        itemNode.put("menuItemId", menuItemId);
        itemNode.put("quantity", 2);
        itemsArray.add(itemNode);
        request.set("items", itemsArray);

        User user = new User();
        user.setId(userId);

        MenuItem menuItem = new MenuItem();
        menuItem.setId(menuItemId);
        menuItem.setPrice(BigDecimal.valueOf(10.00));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setOrderItems(new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(request);

        assertNotNull(result);
        assertNotNull(result.getUser());
        assertEquals(user.getId(), result.getUser().getId());
        assertEquals(DeliveryType.DELIVERY, result.getDeliveryType());
        assertEquals(1, result.getOrderItems().size());
        assertEquals(BigDecimal.valueOf(20.00), result.getTotalPrice());

        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(deliveryRepository).save(any(Delivery.class));
    }


}
