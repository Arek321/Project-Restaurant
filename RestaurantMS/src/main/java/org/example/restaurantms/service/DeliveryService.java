package org.example.restaurantms.service;

import org.example.restaurantms.entity.Delivery;
import org.example.restaurantms.entity.DeliveryStatus;
import org.example.restaurantms.entity.Order;
import org.example.restaurantms.repository.DeliveryRepository;
import org.example.restaurantms.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    public DeliveryService(DeliveryRepository deliveryRepository, OrderRepository orderRepository) {
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public Delivery createDelivery(Long orderId, String address) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Zamówienie o ID " + orderId + " nie istnieje."));

        if (order.getDelivery() != null) {
            throw new RuntimeException("To zamówienie ma już przypisaną dostawę.");
        }

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setAddress(address);
        delivery.setDeliveryDate(LocalDateTime.now());
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);

        return deliveryRepository.save(delivery);
    }

    public void deleteDeliveryById(Long id) {
        if (!deliveryRepository.existsById(id)) {
            throw new RuntimeException("Delivery not found with id: " + id);
        }
        deliveryRepository.deleteById(id);
    }
}
