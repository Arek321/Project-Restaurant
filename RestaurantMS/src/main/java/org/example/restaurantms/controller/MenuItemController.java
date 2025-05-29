package org.example.restaurantms.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.restaurantms.entity.MenuItem;
import org.example.restaurantms.repository.MenuItemRepository;
import org.example.restaurantms.service.MenuItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@Tag(name = "Menu")
public class MenuItemController {

    private final MenuItemService menuItemService;
    public MenuItemController(MenuItemService menuItemService) {this.menuItemService = menuItemService;}

    @Operation(summary = "Get all menu items", description = "Returns a list of all available menu items")
    @ApiResponse(responseCode = "200", description = "Menu items retrieved successfully")
    @GetMapping("/get")
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        List<MenuItem> items = menuItemService.getAllMenuItems();
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Create a new menu item", description = "Adds a new item to the restaurant menu")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Menu item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/post")
    public ResponseEntity<MenuItem> createMenuItem(
            @RequestBody @Parameter(description = "New menu item to add") MenuItem menuItem) {
        MenuItem createdItem = menuItemService.createMenuItem(menuItem);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @Operation(summary = "Partially update a menu item", description = "Updates selected fields of a menu item by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    @PatchMapping("/patch/{id}")
    public ResponseEntity<MenuItem> partiallyUpdateMenuItem(
            @Parameter(description = "ID of a menu item to update")
            @PathVariable Long id,
            @RequestBody @Parameter(description = "Fields to update") MenuItem updates) {

        MenuItem updatedItem = menuItemService.partiallyUpdateMenuItem(id, updates);
        if (updatedItem != null) {
            return ResponseEntity.ok(updatedItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a menu item", description = "Deletes a menu item from the menu by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Menu item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMenuItem(
            @Parameter(description = "ID of menu item to delete")
            @PathVariable Long id) {
        boolean deleted = menuItemService.deleteMenuItem(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
