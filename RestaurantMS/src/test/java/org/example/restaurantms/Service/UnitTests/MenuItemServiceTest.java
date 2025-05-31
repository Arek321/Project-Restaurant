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

    @Test
    @DisplayName("Should create a new menu item")
    public void testCreateMenuItem() {
        MenuItem newItem = new MenuItem();
        newItem.setName("Lasagna");
        newItem.setPrice(BigDecimal.valueOf(25.0));

        when(menuItemRepository.save(newItem)).thenReturn(newItem);

        MenuItem result = menuItemService.createMenuItem(newItem);

        assertNotNull(result);
        assertEquals("Lasagna", result.getName());
        assertEquals(BigDecimal.valueOf(25.0), result.getPrice());
        verify(menuItemRepository, times(1)).save(newItem);
    }

    @Test
    @DisplayName("Should update existing menu item partially")
    public void testPartiallyUpdateMenuItemSuccess() {
        Long itemId = 1L;

        MenuItem existingItem = new MenuItem();
        existingItem.setId(itemId);
        existingItem.setName("OldName");
        existingItem.setDescription("Old desc");
        existingItem.setPrice(BigDecimal.valueOf(10));

        MenuItem updates = new MenuItem();
        updates.setName("NewName");
        updates.setDescription("New desc");
        updates.setPrice(BigDecimal.valueOf(20));

        when(menuItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(menuItemRepository.save(existingItem)).thenReturn(existingItem);

        MenuItem result = menuItemService.partiallyUpdateMenuItem(itemId, updates);

        assertNotNull(result);
        assertEquals("NewName", result.getName());
        assertEquals("New desc", result.getDescription());
        assertEquals(BigDecimal.valueOf(20), result.getPrice());

        verify(menuItemRepository).findById(itemId);
        verify(menuItemRepository).save(existingItem);
    }

    @Test
    @DisplayName("Should return null when trying to update non-existent item")
    public void testPartiallyUpdateMenuItemNotFound() {
        Long itemId = 999L;
        MenuItem updates = new MenuItem();
        updates.setName("DoesNotMatter");

        when(menuItemRepository.findById(itemId)).thenReturn(Optional.empty());

        MenuItem result = menuItemService.partiallyUpdateMenuItem(itemId, updates);

        assertNull(result);
        verify(menuItemRepository).findById(itemId);
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete menu item if it exists")
    public void testDeleteMenuItemSuccess() {
        when(menuItemRepository.existsById(1L)).thenReturn(true);

        boolean result = menuItemService.deleteMenuItem(1L);

        assertTrue(result);
        verify(menuItemRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent item")
    public void testDeleteMenuItemNotFound() {
        when(menuItemRepository.existsById(99L)).thenReturn(false);

        boolean result = menuItemService.deleteMenuItem(99L);

        assertFalse(result);
        verify(menuItemRepository, never()).deleteById(any());
    }


}
