package org.example.restaurantms.service;

import org.springframework.stereotype.Service;
import org.example.restaurantms.entity.MenuItem;
import org.example.restaurantms.repository.MenuItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    public MenuItem partiallyUpdateMenuItem(Long id, MenuItem updates) {
        return menuItemRepository.findById(id).map(item -> {
            if (updates.getName() != null) item.setName(updates.getName());
            if (updates.getDescription() != null) item.setDescription(updates.getDescription());
            if (updates.getPrice() != null) item.setPrice(updates.getPrice());
            if (updates.getAllergens() != null) item.setAllergens(updates.getAllergens());
            return menuItemRepository.save(item);
        }).orElse(null);
    }

    public boolean deleteMenuItem(Long id) {
        if (menuItemRepository.existsById(id)) {
            menuItemRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
