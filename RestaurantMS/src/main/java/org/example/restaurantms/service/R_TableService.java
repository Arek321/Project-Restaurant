package org.example.restaurantms.service;

import org.example.restaurantms.entity.R_Table;
import org.example.restaurantms.repository.R_TableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class R_TableService {
    private final R_TableRepository rTableRepository;

    public R_TableService(R_TableRepository rTableRepository) {
        this.rTableRepository = rTableRepository;
    }

    public List<R_Table> getAllTables() {
        return rTableRepository.findAll();
    }

    public R_Table addTable(R_Table table) {
        return rTableRepository.save(table);
    }

    public R_Table getTableById(Long id) {
        return rTableRepository.findById(id).orElse(null);
    }

    public R_Table updateTable(Long id, R_Table updatedData) {
        return rTableRepository.findById(id).map(existingTable -> {
            if (updatedData.getTableNumber() != 0) {
                existingTable.setTableNumber(updatedData.getTableNumber());
            }
            if (updatedData.getSeatsNumber() != 0) {
                existingTable.setSeatsNumber(updatedData.getSeatsNumber());
            }
            return rTableRepository.save(existingTable);
        }).orElse(null);
    }

    public boolean deleteTable(Long id) {
        if (rTableRepository.existsById(id)) {
            rTableRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
