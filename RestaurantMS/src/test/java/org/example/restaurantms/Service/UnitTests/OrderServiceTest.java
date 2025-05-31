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

    @Test
    @DisplayName("Should get all orders")
    public void testGetAllOrders() {
        Order o1 = new Order();
        Order o2 = new Order();

        when(orderRepository.findAll()).thenReturn(Arrays.asList(o1, o2));

        List<Order> orders = orderService.getAllOrders();

        assertEquals(2, orders.size());
        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("Should get order by ID if it exists")
    public void testGetOrderByIdFound() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty optional if order not found")
    public void testGetOrderByIdNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.getOrderById(99L);

        assertFalse(result.isPresent());
        verify(orderRepository).findById(99L);
    }

    @Test
    @DisplayName("Should delete order with items and delivery if present")
    public void testDeleteOrderSuccess() {
        Order order = new Order();
        order.setId(1L);

        OrderItem item1 = new OrderItem();
        OrderItem item2 = new OrderItem();
        order.setOrderItems(Arrays.asList(item1, item2));

        Delivery delivery = new Delivery();
        order.setDelivery(delivery);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        boolean result = orderService.deleteOrderById(1L);

        assertTrue(result);
        verify(orderItemRepository).deleteAll(order.getOrderItems());
        verify(deliveryRepository).delete(delivery);
        verify(orderRepository).delete(order);
    }

    @Test
    @DisplayName("Should return false if trying to delete non-existent order")
    public void testDeleteOrderNotFound() {
        when(orderRepository.findById(123L)).thenReturn(Optional.empty());

        boolean result = orderService.deleteOrderById(123L);

        assertFalse(result);
        verify(orderRepository, never()).delete(any());
    }

}
