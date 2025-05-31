package org.example.restaurantms.Service.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.restaurantms.entity.R_Table;
import org.example.restaurantms.entity.Reservation;
import org.example.restaurantms.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserReservationTableIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateReservationWithUserAndTable() throws Exception {
        // tworze usera
        ObjectNode userRequest = objectMapper.createObjectNode();
        userRequest.put("username", "reservation_user");
        userRequest.put("password", "test123");
        userRequest.put("email", "email@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(userRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User createdUser = objectMapper.readValue(userResponse, User.class);

        // tworze stolik
        ObjectNode tableRequest = objectMapper.createObjectNode();
        tableRequest.put("tableNumber", 10);
        tableRequest.put("seatsNumber", 4);

        String tableResponse = mockMvc.perform(post("/api/tables/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(tableRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        R_Table createdTable = objectMapper.readValue(tableResponse, R_Table.class);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime safeTime = now.withHour(12).withMinute(0).withSecond(0).withNano(0);

        // tworze rezerwacje
        String startTime = safeTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        mockMvc.perform(post("/api/reservations/post")
                        .param("userId", String.valueOf(createdUser.getId()))
                        .param("tableId", String.valueOf(createdTable.getId()))
                        .param("startTime", startTime)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(createdUser.getId().intValue())))
                .andExpect(jsonPath("$.tableId", is(createdTable.getId().intValue())))
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.endTime").exists());
    }
}
