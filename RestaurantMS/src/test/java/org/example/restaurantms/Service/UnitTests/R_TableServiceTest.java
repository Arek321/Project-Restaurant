package org.example.restaurantms.Service.UnitTests;

import org.example.restaurantms.entity.R_Table;
import org.example.restaurantms.repository.R_TableRepository;
import org.example.restaurantms.service.R_TableService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class R_TableServiceTest {

    private R_TableRepository rTableRepository;
    private R_TableService rTableService;

    @BeforeEach
    public void setUp() {
        rTableRepository = mock(R_TableRepository.class);
        rTableService = new R_TableService(rTableRepository);
    }

    @Test
    @DisplayName("Should return all tables")
    public void testGetAllTables() {
        R_Table table1 = new R_Table();
        table1.setTableNumber(1);

        R_Table table2 = new R_Table();
        table2.setTableNumber(2);

        when(rTableRepository.findAll()).thenReturn(Arrays.asList(table1, table2));

        List<R_Table> result = rTableService.getAllTables();

        assertEquals(2, result.size());
        verify(rTableRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should add a new table")
    public void testAddTable() {
        R_Table table = new R_Table();
        table.setTableNumber(5);
        table.setSeatsNumber(4);

        when(rTableRepository.save(table)).thenReturn(table);

        R_Table result = rTableService.addTable(table);

        assertNotNull(result);
        assertEquals(5, result.getTableNumber());
        verify(rTableRepository, times(1)).save(table);
    }

    @Test
    @DisplayName("Should return table by ID if exists")
    public void testGetTableByIdExists() {
        R_Table table = new R_Table();
        table.setId(1L);
        table.setTableNumber(10);

        when(rTableRepository.findById(1L)).thenReturn(Optional.of(table));

        R_Table result = rTableService.getTableById(1L);

        assertNotNull(result);
        assertEquals(10, result.getTableNumber());
        verify(rTableRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return null if table with given ID does not exist")
    public void testGetTableByIdNotExists() {
        when(rTableRepository.findById(99L)).thenReturn(Optional.empty());

        R_Table result = rTableService.getTableById(99L);

        assertNull(result);
        verify(rTableRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should update table fields if valid data provided")
    public void testUpdateTableSuccess() {
        R_Table existingTable = new R_Table();
        existingTable.setId(1L);
        existingTable.setTableNumber(1);
        existingTable.setSeatsNumber(4);

        R_Table updates = new R_Table();
        updates.setTableNumber(2);
        updates.setSeatsNumber(6);

        when(rTableRepository.findById(1L)).thenReturn(Optional.of(existingTable));
        when(rTableRepository.save(existingTable)).thenReturn(existingTable);

        R_Table result = rTableService.updateTable(1L, updates);

        assertNotNull(result);
        assertEquals(2, result.getTableNumber());
        assertEquals(6, result.getSeatsNumber());
        verify(rTableRepository).save(existingTable);
    }

    @Test
    @DisplayName("Should return null when updating non-existent table")
    public void testUpdateTableNotExists() {
        R_Table updates = new R_Table();
        updates.setTableNumber(2);
        updates.setSeatsNumber(6);

        when(rTableRepository.findById(100L)).thenReturn(Optional.empty());

        R_Table result = rTableService.updateTable(100L, updates);

        assertNull(result);
        verify(rTableRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete table if it exists")
    public void testDeleteTableSuccess() {
        when(rTableRepository.existsById(1L)).thenReturn(true);

        boolean result = rTableService.deleteTable(1L);

        assertTrue(result);
        verify(rTableRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent table")
    public void testDeleteTableNotExists() {
        when(rTableRepository.existsById(100L)).thenReturn(false);

        boolean result = rTableService.deleteTable(100L);

        assertFalse(result);
        verify(rTableRepository, never()).deleteById(any());
    }
}
