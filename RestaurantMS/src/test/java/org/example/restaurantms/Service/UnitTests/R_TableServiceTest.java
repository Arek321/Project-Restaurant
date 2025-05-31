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


}
