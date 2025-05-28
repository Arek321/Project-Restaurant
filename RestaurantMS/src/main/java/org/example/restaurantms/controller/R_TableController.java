package org.example.restaurantms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.restaurantms.entity.R_Table;
import org.example.restaurantms.repository.R_TableRepository;
import org.example.restaurantms.service.R_TableService;
import org.example.restaurantms.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@Tag(name = "R_Table")
public class R_TableController {

    private final R_TableService rTableService;
    public R_TableController(R_TableService rTableService) {
        this.rTableService = rTableService;
    }


    @Operation(summary = "Get all tables", description = "Returns a list of all restaurant tables")
    @GetMapping
    public ResponseEntity<List<R_Table>> getAllTables() {
        return ResponseEntity.ok(rTableService.getAllTables());
    }

    @Operation(summary = "Create a new Table", description = "Allows to create a new restaurant table")
    @ApiResponse(responseCode = "201", description = "Table created successfully")
    @PostMapping("/create")
    public ResponseEntity<R_Table> createTable(
            @RequestBody @Parameter(description = "Table object that needs to be created") R_Table table) {
        R_Table createdTable = rTableService.addTable(table);
        return new ResponseEntity<>(createdTable, HttpStatus.CREATED);
    }
}
