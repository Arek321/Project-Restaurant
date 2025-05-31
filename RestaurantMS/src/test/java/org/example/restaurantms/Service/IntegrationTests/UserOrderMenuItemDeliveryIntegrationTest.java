package org.example.restaurantms.Service.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.restaurantms.DTO.ReservationDTO;
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
public class UserOrderMenuItemDeliveryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testCreateOrderWithDelivery() throws Exception {
        // usera
        ObjectNode user = objectMapper.createObjectNode();
        user.put("username", "order_user");
        user.put("password", "test123");
        user.put("email", "order@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = objectMapper.readTree(userResponse).get("id").asLong();

        // tworze pozycje w menu
        ObjectNode menuItem = objectMapper.createObjectNode();
        menuItem.put("name", "Burger");
        menuItem.put("price", 25.00);
        menuItem.put("description", "Test burger");

        String itemResponse = mockMvc.perform(post("/api/menu/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(menuItem.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long menuItemId = objectMapper.readTree(itemResponse).get("id").asLong();

        // tworze zamówienie z dostawą
        ObjectNode request = objectMapper.createObjectNode();
        request.put("userId", userId);
        request.put("deliveryType", "DELIVERY");
        request.put("deliveryAddress", "ul. Zamówiona 123");

        ObjectNode item = objectMapper.createObjectNode();
        item.put("menuItemId", menuItemId);
        item.put("quantity", 2);
        request.set("items", objectMapper.createArrayNode().add(item));

        mockMvc.perform(post("/api/orders/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.totalPrice").value(50.00));
    }

    @Test
    public void testCreateOrderWithoutDelivery() throws Exception {
        // user
        ObjectNode user = objectMapper.createObjectNode();
        user.put("username", "onsite_user");
        user.put("password", "test123");
        user.put("email", "onsite@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = objectMapper.readTree(userResponse).get("id").asLong();

        // menu
        ObjectNode menuItem = objectMapper.createObjectNode();
        menuItem.put("name", "Pizza");
        menuItem.put("price", 30.00);
        menuItem.put("description", "Test pizza");

        String itemResponse = mockMvc.perform(post("/api/menu/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(menuItem.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long menuItemId = objectMapper.readTree(itemResponse).get("id").asLong();

        // zamówienie na miejscu
        ObjectNode request = objectMapper.createObjectNode();
        request.put("userId", userId);
        request.put("deliveryType", "ON_SITE");

        ObjectNode item = objectMapper.createObjectNode();
        item.put("menuItemId", menuItemId);
        item.put("quantity", 1);
        request.set("items", objectMapper.createArrayNode().add(item));

        mockMvc.perform(post("/api/orders/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.totalPrice").value(30.00))
                .andExpect(jsonPath("$.delivery").doesNotExist());
    }

    @Test
    public void testCreateOrderWithInvalidMenuItem() throws Exception {
        // tworzymy użytkownika
        ObjectNode user = objectMapper.createObjectNode();
        user.put("username", "invalid_item_user");
        user.put("password", "test123");
        user.put("email", "invalid@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = objectMapper.readTree(userResponse).get("id").asLong();

        // próbujemy zamówić nieistniejący produkt
        ObjectNode request = objectMapper.createObjectNode();
        request.put("userId", userId);
        request.put("deliveryType", "ON_SITE");

        ObjectNode fakeItem = objectMapper.createObjectNode();
        fakeItem.put("menuItemId", 9999); // NIEISTNIEJĄCY ID
        fakeItem.put("quantity", 1);
        request.set("items", objectMapper.createArrayNode().add(fakeItem));

        mockMvc.perform(post("/api/orders/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.toString()))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        Assertions.assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result ->
                        Assertions.assertTrue(
                                result.getResolvedException().getMessage().contains("MenuItem not found")
                        ));
    }

    @Test
    public void testDeleteExistingOrder() throws Exception {
        //tworze usera
        ObjectNode user = objectMapper.createObjectNode();
        user.put("username", "delete_user");
        user.put("password", "test123");
        user.put("email", "delete@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = objectMapper.readTree(userResponse).get("id").asLong();

        // Tworze poz w menu
        ObjectNode menuItem = objectMapper.createObjectNode();
        menuItem.put("name", "Pizza");
        menuItem.put("price", 30.0);
        menuItem.put("description", "Test pizza");

        String menuItemResponse = mockMvc.perform(post("/api/menu/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(menuItem.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long menuItemId = objectMapper.readTree(menuItemResponse).get("id").asLong();

        // tworze zamowienie z dostawa
        ObjectNode orderRequest = objectMapper.createObjectNode();
        orderRequest.put("userId", userId);
        orderRequest.put("deliveryType", "DELIVERY");
        orderRequest.put("deliveryAddress", "ul. Pizza 7");

        ObjectNode item = objectMapper.createObjectNode();
        item.put("menuItemId", menuItemId);
        item.put("quantity", 1);
        orderRequest.set("items", objectMapper.createArrayNode().add(item));

        String orderResponse = mockMvc.perform(post("/api/orders/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(orderResponse).get("id").asLong();

        // usuwam zamowienie
        mockMvc.perform(delete("/api/orders/delete/" + orderId))
                .andExpect(status().isNoContent());

        // check czy juz nie istnieje
        mockMvc.perform(get("/api/orders/get/" + orderId))
                .andExpect(status().isNotFound());
    }

    //szybki test pokrywajacy usuwanie nieistniejacego zamówienia
    @Test
    public void testDeleteNonExistingOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/delete/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetOrderById() throws Exception {
        // tworze usera
        ObjectNode user = objectMapper.createObjectNode();
        user.put("username", "getorder_user");
        user.put("password", "test123");
        user.put("email", "getorder@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = objectMapper.readTree(userResponse).get("id").asLong();

        // tworze pozycje w menu
        ObjectNode menuItem = objectMapper.createObjectNode();
        menuItem.put("name", "Salad");
        menuItem.put("price", 15.0);
        menuItem.put("description", "Healthy salad");

        String itemResponse = mockMvc.perform(post("/api/menu/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(menuItem.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long menuItemId = objectMapper.readTree(itemResponse).get("id").asLong();

        // tworze order
        ObjectNode orderRequest = objectMapper.createObjectNode();
        orderRequest.put("userId", userId);
        orderRequest.put("deliveryType", "ON_SITE"); // Bez dostawy

        ObjectNode item = objectMapper.createObjectNode();
        item.put("menuItemId", menuItemId);
        item.put("quantity", 3);
        orderRequest.set("items", objectMapper.createArrayNode().add(item));

        String orderResponse = mockMvc.perform(post("/api/orders/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(orderResponse).get("id").asLong();

        // pobieram po id
        mockMvc.perform(get("/api/orders/get/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.totalPrice").value(45.0));
    }

    @Test
    public void testGetNonExistingOrderById() throws Exception {
        mockMvc.perform(get("/api/orders/get/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteOrderById() throws Exception {
        // tworze usera
        ObjectNode user = objectMapper.createObjectNode();
        user.put("username", "delete_user");
        user.put("password", "test123");
        user.put("email", "delete@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = objectMapper.readTree(userResponse).get("id").asLong();

        // tworze pozycje w menu
        ObjectNode menuItem = objectMapper.createObjectNode();
        menuItem.put("name", "Pizza");
        menuItem.put("price", 30.00);
        menuItem.put("description", "Margherita");

        String itemResponse = mockMvc.perform(post("/api/menu/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(menuItem.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long menuItemId = objectMapper.readTree(itemResponse).get("id").asLong();

        // tworze zamowienie z dostawa
        ObjectNode orderRequest = objectMapper.createObjectNode();
        orderRequest.put("userId", userId);
        orderRequest.put("deliveryType", "DELIVERY");
        orderRequest.put("deliveryAddress", "ul. Usuwana 1");

        ObjectNode item = objectMapper.createObjectNode();
        item.put("menuItemId", menuItemId);
        item.put("quantity", 1);
        orderRequest.set("items", objectMapper.createArrayNode().add(item));

        String orderResponse = mockMvc.perform(post("/api/orders/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(orderResponse).get("id").asLong();

        // usuwam zamowienie
        mockMvc.perform(delete("/api/orders/delete/" + orderId))
                .andExpect(status().isNoContent());

        // probuje je pobrac, ale nie istnieje
        mockMvc.perform(get("/api/orders/get/" + orderId))
                .andExpect(status().isNotFound());
    }



    @Test
    public void testGetAllOrders() throws Exception {
        // tworze usera
        ObjectNode user = objectMapper.createObjectNode();
        user.put("username", "list_user");
        user.put("password", "test123");
        user.put("email", "list@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = objectMapper.readTree(userResponse).get("id").asLong();

        // tworze item w menu
        ObjectNode menuItem = objectMapper.createObjectNode();
        menuItem.put("name", "Frytki");
        menuItem.put("price", 10.00);
        menuItem.put("description", "Ziemniaczane");

        String itemResponse = mockMvc.perform(post("/api/menu/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(menuItem.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long menuItemId = objectMapper.readTree(itemResponse).get("id").asLong();

        // tworze zamowienie z typem DELIVERY (bo tylko ten enum istnieje)
        ObjectNode orderRequest = objectMapper.createObjectNode();
        orderRequest.put("userId", userId);
        orderRequest.put("deliveryType", "DELIVERY");  // <- poprawiona wartość
        orderRequest.put("deliveryAddress", "ul. Lista 456"); // bo delivery wymaga adresu

        ObjectNode item = objectMapper.createObjectNode();
        item.put("menuItemId", menuItemId);
        item.put("quantity", 3);
        orderRequest.set("items", objectMapper.createArrayNode().add(item));

        mockMvc.perform(post("/api/orders/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest.toString()))
                .andExpect(status().isCreated());

        // pobieram wszystkie zamowienia
        mockMvc.perform(get("/api/orders/get"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"quantity\":3")));
    }

    @Test
    public void testCreateAndGetMenuItem() throws Exception {
        ObjectNode menuItem = objectMapper.createObjectNode();
        menuItem.put("name", "Pizza");
        menuItem.put("price", 30.00);
        menuItem.put("description", "Cheesy pizza");

        mockMvc.perform(post("/api/menu/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(menuItem.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Pizza"));

        mockMvc.perform(get("/api/menu/get"))
                .andExpect(status().isOk());

    }

    @Test
    public void testPatchMenuItem() throws Exception {
        // tworze menuitem
        ObjectNode item = objectMapper.createObjectNode();
        item.put("name", "Makaron");
        item.put("price", 20.0);
        item.put("description", "Z sosem pomidorowym");

        String response = mockMvc.perform(post("/api/menu/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(item.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        //patchuje
        ObjectNode patch = objectMapper.createObjectNode();
        patch.put("price", 25.0);

        mockMvc.perform(patch("/api/menu/patch/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(25.0));
    }

    @Test
    public void testDeleteMenuItem() throws Exception {
        ObjectNode item = objectMapper.createObjectNode();
        item.put("name", "Lody");
        item.put("price", 10.0);
        item.put("description", "Waniliowe");

        String response = mockMvc.perform(post("/api/menu/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(item.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/menu/delete/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/menu/delete/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateDelivery() throws Exception {
        // tworze usera
        ObjectNode user = objectMapper.createObjectNode();
        user.put("username", "delivery_test_user");
        user.put("password", "pass123");
        user.put("email", "deliveryuser@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long userId = objectMapper.readTree(userResponse).get("id").asLong();

        // tworze menu item
        ObjectNode menuItem = objectMapper.createObjectNode();
        menuItem.put("name", "Burger");
        menuItem.put("price", 25.0);
        menuItem.put("description", "Beefy");

        String itemResponse = mockMvc.perform(post("/api/menu/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(menuItem.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long menuItemId = objectMapper.readTree(itemResponse).get("id").asLong();

        // 3. tworze delivery
        ObjectNode orderRequest = objectMapper.createObjectNode();
        orderRequest.put("userId", userId);
        orderRequest.put("deliveryType", "ON_SITE");

        ObjectNode item = objectMapper.createObjectNode();
        item.put("menuItemId", menuItemId);
        item.put("quantity", 2);
        orderRequest.set("items", objectMapper.createArrayNode().add(item));

        String orderResponse = mockMvc.perform(post("/api/orders/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long orderId = objectMapper.readTree(orderResponse).get("id").asLong();

        // tworze delivery
        mockMvc.perform(post("/api/deliveries/post")
                        .param("orderId", orderId.toString())
                        .param("address", "Testowa 123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Testowa 123"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    public void testGetAllDeliveries() throws Exception {
        mockMvc.perform(get("/api/deliveries/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testDeleteDelivery() throws Exception {
        // tworze usera
        ObjectNode user = objectMapper.createObjectNode();
        user.put("username", "delete_delivery_user");
        user.put("password", "pass123");
        user.put("email", "delete@example.com");

        String userResponse = mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long userId = objectMapper.readTree(userResponse).get("id").asLong();

        //  Tworzenie pozycji w menu
        ObjectNode menuItem = objectMapper.createObjectNode();
        menuItem.put("name", "Makaron");
        menuItem.put("price", 20.0);
        menuItem.put("description", "Z sosem");

        String itemResponse = mockMvc.perform(post("/api/menu/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(menuItem.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long menuItemId = objectMapper.readTree(itemResponse).get("id").asLong();

        // 3. Tworzenie zamówienia
        ObjectNode orderRequest = objectMapper.createObjectNode();
        orderRequest.put("userId", userId);
        orderRequest.put("deliveryType", "ON_SITE");

        ObjectNode item = objectMapper.createObjectNode();
        item.put("menuItemId", menuItemId);
        item.put("quantity", 1);
        orderRequest.set("items", objectMapper.createArrayNode().add(item));

        String orderResponse = mockMvc.perform(post("/api/orders/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long orderId = objectMapper.readTree(orderResponse).get("id").asLong();

        // tworze dostawe
        String deliveryResponse = mockMvc.perform(post("/api/deliveries/post")
                        .param("orderId", orderId.toString())
                        .param("address", "ul. Kasacja 999"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long deliveryId = objectMapper.readTree(deliveryResponse).get("id").asLong();

        // usuwam dostawe
        mockMvc.perform(delete("/api/deliveries/delete/" + deliveryId))
                .andExpect(status().isOk());
    }

}
