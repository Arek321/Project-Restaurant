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
}
