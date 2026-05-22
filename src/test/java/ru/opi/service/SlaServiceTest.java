package ru.opi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.opi.model.SlaRecord;
import ru.opi.repository.SlaRecordRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlaServiceTest {

    @Mock
    private SlaRecordRepository slaRecordRepository;

    @InjectMocks
    private SlaService slaService;

    private SlaRecord testSlaRecord;

    @BeforeEach
    void setUp() {
        testSlaRecord = new SlaRecord();
        testSlaRecord.setId(1);
        testSlaRecord.setSlaForecast(true);
    }

    @Test
    void testSaveSla() {
        when(slaRecordRepository.save(any(SlaRecord.class))).thenReturn(testSlaRecord);

        slaService.saveSla(testSlaRecord);

        verify(slaRecordRepository, times(1)).save(any(SlaRecord.class));
    }

    @Test
    void testFindAll() {
        List<SlaRecord> slaRecords = Arrays.asList(testSlaRecord);
        when(slaRecordRepository.findAll()).thenReturn(slaRecords);

        List<SlaRecord> result = slaService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        verify(slaRecordRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        when(slaRecordRepository.findAll()).thenReturn(Arrays.asList());

        List<SlaRecord> result = slaService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(slaRecordRepository, times(1)).findAll();
    }
}