package org.example.restaurantms.Service.UnitTests;

import org.example.restaurantms.entity.MenuItem;
import org.example.restaurantms.repository.MenuItemRepository;
import org.example.restaurantms.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MenuItemServiceTest {

    private MenuItemRepository menuItemRepository;
    private MenuItemService menuItemService;

    @BeforeEach
    public void setUp() {
        menuItemRepository = mock(MenuItemRepository.class);
        menuItemService = new MenuItemService(menuItemRepository);
    }

    @Test
    @DisplayName("Should return all menu items")
    public void testGetAllMenuItems() {
        MenuItem item1 = new MenuItem();
        item1.setName("Pizza");

        MenuItem item2 = new MenuItem();
        item2.setName("Burger");

        when(menuItemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        List<MenuItem> result = menuItemService.getAllMenuItems();

        assertEquals(2, result.size());
        verify(menuItemRepository, times(1)).findAll();
    }


}
