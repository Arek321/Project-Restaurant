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
    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        List<MenuItem> items = menuItemService.getAllMenuItems();
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Create a new menu item", description = "Adds a new item to the restaurant menu")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Menu item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })

    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(
            @RequestBody @Parameter(description = "New menu item to add") MenuItem menuItem) {
        MenuItem createdItem = menuItemService.createMenuItem(menuItem);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }
}
