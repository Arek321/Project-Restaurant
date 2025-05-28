// to jest encja pośrednia pomiędzy Order a MenuItem -> nie robiłem @ManyToMany, bo potrzebowałem
// dodać quantity i price, czego nie mógłbym zrobić zwyczajnie mapująć ManyToMany
package org.example.restaurantms.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne()
    @JoinColumn(name = "item_id")
    @JsonManagedReference(value = "item-orderItems")
    private MenuItem item;

    private int quantity;
    private BigDecimal itemPrice; // to aby cena pozostała niezmienna w czasie dot. zamówienia
}
