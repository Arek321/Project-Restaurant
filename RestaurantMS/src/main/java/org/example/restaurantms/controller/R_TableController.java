package org.example.restaurantms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.restaurantms.entity.R_Table;
import org.example.restaurantms.repository.R_TableRepository;
import org.example.restaurantms.service.R_TableService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@Tag(name = "Table")
public class R_TableController {

    private final R_TableService rTableService;
    public R_TableController(R_TableService rTableService) {
        this.rTableService = rTableService;
    }


    @Operation(summary = "Get all tables", description = "Returns a list of all restaurant tables")
    @GetMapping("/get")
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

    @Operation(summary = "Get a table by ID", description = "Returns a single table by its ID")
    @GetMapping("/get/{id}")
    public ResponseEntity<R_Table> getTableById(
            @Parameter(description = "ID of a table to return")
            @PathVariable Long id)
    {
        R_Table table = rTableService.getTableById(id);
        if (table != null) {
            return ResponseEntity.ok(table);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update table data", description = "Allows to partially update an existing restaurant table")
    @ApiResponse(responseCode = "200", description = "Table updated successfully")
    @ApiResponse(responseCode = "404", description = "Table not found")
    @PatchMapping("/patch/{id}")
    public ResponseEntity<R_Table> updateTable(
            @Parameter(description = "ID of a table to update")
            @PathVariable Long id,
            @RequestBody R_Table updatedData) {

        R_Table updatedTable = rTableService.updateTable(id, updatedData);
        if (updatedTable != null) {
            return ResponseEntity.ok(updatedTable);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a table by ID", description = "Deletes a table from the system using its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Table deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Table not found")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTable(
            @Parameter(description = "ID of a table to delete")
            @PathVariable Long id)
    {
        boolean deleted = rTableService.deleteTable(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
