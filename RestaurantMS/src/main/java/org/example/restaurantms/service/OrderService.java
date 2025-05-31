package org.example.restaurantms.service;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.example.restaurantms.entity.*;
import org.example.restaurantms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryRepository deliveryRepository;

    public Order createOrder(JsonNode request) {
        Long userId = request.get("userId").asLong();
        DeliveryType deliveryType = DeliveryType.valueOf(request.get("deliveryType").asText());

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryType(deliveryType);
        order.setTotalPrice(BigDecimal.ZERO);

        Order savedOrder = orderRepository.save(order);

        // tworze orderitems
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (JsonNode itemNode : request.get("items")) {
            Long menuItemId = itemNode.get("menuItemId").asLong();
            int quantity = itemNode.get("quantity").asInt();

            MenuItem menuItem = menuItemRepository.findById(menuItemId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MenuItem not found"));


            BigDecimal itemPrice = menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setItem(menuItem);
            orderItem.setQuantity(quantity);
            orderItem.setItemPrice(itemPrice);

            orderItems.add(orderItemRepository.save(orderItem));
            totalPrice = totalPrice.add(itemPrice);
        }

        savedOrder.setOrderItems(orderItems);
        savedOrder.setTotalPrice(totalPrice);

        // obługa dostawy (jesli dotyczy zamówienia)
        if (deliveryType == DeliveryType.DELIVERY && request.has("deliveryAddress")) {
            Delivery delivery = new Delivery();
            delivery.setOrder(savedOrder);
            delivery.setDeliveryDate(LocalDateTime.now().plusMinutes(45));
            delivery.setStatus(DeliveryStatus.IN_PROGRESS);
            delivery.setAddress(request.get("deliveryAddress").asText());
            deliveryRepository.save(delivery);
            savedOrder.setDelivery(delivery);
        }

        return orderRepository.save(savedOrder);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public boolean deleteOrderById(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();

            //najpierw usuwamy pozycje zamówienia
            orderItemRepository.deleteAll(order.getOrderItems());

            // usuwamy dostawe, jeśli istnieje
            if (order.getDelivery() != null) {
                deliveryRepository.delete(order.getDelivery());
            }

            // usuwam samo zamówienie
            orderRepository.delete(order);

            return true;
        } else {
            return false;
        }

    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
}
