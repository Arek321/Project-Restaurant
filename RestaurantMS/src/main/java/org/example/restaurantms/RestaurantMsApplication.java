package org.example.restaurantms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("org.example.restaurantms.entity")
@EnableJpaRepositories("org.example.restaurantms.repository")
@SpringBootApplication
public class RestaurantMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantMsApplication.class, args);
    }

}
