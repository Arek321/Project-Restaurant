package org.example.restaurantms.Service.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.restaurantms.DTO.ReservationDTO;
import org.example.restaurantms.entity.R_Table;
import org.example.restaurantms.entity.Reservation;
import org.example.restaurantms.entity.User;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.web.server.ResponseStatusException;
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

    @Test
    public void testGetAllReservations() throws Exception {
        mockMvc.perform(get("/api/reservations/get")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateReservation() throws Exception {
        //  Tworzenie użytkownika
        ObjectNode userRequest = objectMapper.createObjectNode();
        userRequest.put("username", "update_user");
        userRequest.put("password", "test123");
        userRequest.put("email", "update@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(userRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User user = objectMapper.readValue(userResponse, User.class);

        //  Tworzenie stolika
        ObjectNode tableRequest = objectMapper.createObjectNode();
        tableRequest.put("tableNumber", 55);
        tableRequest.put("seatsNumber", 6);

        String tableResponse = mockMvc.perform(post("/api/tables/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(tableRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        R_Table table = objectMapper.readValue(tableResponse, R_Table.class);

        // Tworzenie rezerwacji
        String initialStartTime = LocalDateTime.now()
                .withSecond(0)
                .withNano(0)
                .withHour(12)
                .withMinute(0)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String reservationResponse = mockMvc.perform(post("/api/reservations/post")
                        .param("userId", String.valueOf(user.getId()))
                        .param("tableId", String.valueOf(table.getId()))
                        .param("startTime", initialStartTime)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ReservationDTO reservation = objectMapper.readValue(reservationResponse, ReservationDTO.class);

        // Aktualizacja rezerwacji
        String newStartTime = LocalDateTime.now()
                .withHour(13)  // <-- między 10 a 20
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        mockMvc.perform(patch("/api/reservations/patch/{id}", reservation.getId())
                        .param("startTime", newStartTime)
                        .param("tableId", String.valueOf(table.getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservation.getId()))
                .andExpect(jsonPath("$.startTime").value(newStartTime));
    }

    @Test
    public void testDeleteReservation() throws Exception {
        // tworzenie usera
        ObjectNode userRequest = objectMapper.createObjectNode();
        userRequest.put("username", "delete_user");
        userRequest.put("password", "test123");
        userRequest.put("email", "delete@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User user = objectMapper.readValue(userResponse, User.class);

        // tworzenie stolika
        ObjectNode tableRequest = objectMapper.createObjectNode();
        tableRequest.put("tableNumber", 44);
        tableRequest.put("seatsNumber", 2);

        String tableResponse = mockMvc.perform(post("/api/tables/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tableRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        R_Table table = objectMapper.readValue(tableResponse, R_Table.class);

        // tworzenie rezerwacji
        String startTime = LocalDateTime.now()
                .withHour(11)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String reservationResponse = mockMvc.perform(post("/api/reservations/post")
                        .param("userId", String.valueOf(user.getId()))
                        .param("tableId", String.valueOf(table.getId()))
                        .param("startTime", startTime))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ReservationDTO reservation = objectMapper.readValue(reservationResponse, ReservationDTO.class);

        // usuwanie rezerwacji
        mockMvc.perform(delete("/api/reservations/delete/{id}", reservation.getId()))
                .andExpect(status().isNoContent());

        // proba ponownego usuniecia -notfoundexception
        mockMvc.perform(delete("/api/reservations/delete/{id}", reservation.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateReservationWithConflict() throws Exception {
        // Tworzenie użytkownika
        ObjectNode userRequest = objectMapper.createObjectNode();
        userRequest.put("username", "conflict_user");
        userRequest.put("password", "test123");
        userRequest.put("email", "conflict@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User user = objectMapper.readValue(userResponse, User.class);

        // Tworzenie stolika
        ObjectNode tableRequest = objectMapper.createObjectNode();
        tableRequest.put("tableNumber", 33);
        tableRequest.put("seatsNumber", 4);

        String tableResponse = mockMvc.perform(post("/api/tables/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tableRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        R_Table table = objectMapper.readValue(tableResponse, R_Table.class);

        // Czas rezerwacji
        String startTime = LocalDateTime.now()
                .withHour(13).withMinute(0).withSecond(0).withNano(0)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Pierwsza rezerwacja
        mockMvc.perform(post("/api/reservations/post")
                        .param("userId", String.valueOf(user.getId()))
                        .param("tableId", String.valueOf(table.getId()))
                        .param("startTime", startTime))
                .andExpect(status().isCreated());

        // Druga rezerwacja - konflikt
        mockMvc.perform(post("/api/reservations/post")
                        .param("userId", String.valueOf(user.getId()))
                        .param("tableId", String.valueOf(table.getId()))
                        .param("startTime", startTime))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        Assertions.assertTrue(
                                result.getResolvedException() instanceof ResponseStatusException &&
                                        ((ResponseStatusException) result.getResolvedException()).getReason()
                                                .contains("Stolik jest już zarezerwowany")
                        )
                );
    }

    @Test
    public void testReservationOutsideOpeningHours() throws Exception {
        // Tworzenie użytkownika
        ObjectNode userRequest = objectMapper.createObjectNode();
        userRequest.put("username", "night_user");
        userRequest.put("password", "test123");
        userRequest.put("email", "night@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequest.toString()))
                .andReturn().getResponse().getContentAsString();
        User user = objectMapper.readValue(userResponse, User.class);

        // Tworzenie stolika
        ObjectNode tableRequest = objectMapper.createObjectNode();
        tableRequest.put("tableNumber", 99);
        tableRequest.put("seatsNumber", 4);

        String tableResponse = mockMvc.perform(post("/api/tables/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tableRequest.toString()))
                .andReturn().getResponse().getContentAsString();
        R_Table table = objectMapper.readValue(tableResponse, R_Table.class);

        // Rezerwacja poza godzinami (21:00)
        String lateTime = LocalDateTime.now()
                .withHour(21).withMinute(0).withSecond(0).withNano(0)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        mockMvc.perform(post("/api/reservations/post")
                        .param("userId", String.valueOf(user.getId()))
                        .param("tableId", String.valueOf(table.getId()))
                        .param("startTime", lateTime))
                .andExpect(status().isBadRequest()) // POPRAWKA TUTAJ
                .andExpect(result ->
                        Assertions.assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> {
                    Throwable resolved = result.getResolvedException();
                    Assertions.assertTrue(resolved instanceof ResponseStatusException);
                    ResponseStatusException ex = (ResponseStatusException) resolved;
                    Assertions.assertTrue(ex.getReason().contains("Restauracja jest czynna od 10:00 do 22:00"));
                });
    }

}
