package ru.opi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.opi.model.*;
import ru.opi.repository.EngineerRepository;
import ru.opi.repository.RequestRepository;
import ru.opi.repository.SlaRecordRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanningServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private EngineerRepository engineerRepository;

    @Mock
    private SlaRecordRepository slaRecordRepository;

    @InjectMocks
    private PlanningService planningService;

    private Request newRequest;
    private Engineer engineer;

    @BeforeEach
    void setUp() {
        newRequest = new Request();
        newRequest.setId(1);
        newRequest.setSubject("Тестовая заявка");
        newRequest.setPriority(Priority.СРЕДНИЙ);
        newRequest.setStatus(Status.НОВАЯ);

        engineer = new Engineer();
        engineer.setId(1);
        engineer.setFio("Иванов И.И.");
        engineer.setLineLevel(LineLevel.TWO);
        engineer.setActive(true);
    }

    @Test
    void testRunPlanning_NoPendingRequests() {
        when(requestRepository.findByStatus(Status.НОВАЯ)).thenReturn(Collections.emptyList());
        when(engineerRepository.findByActiveTrue()).thenReturn(Arrays.asList(engineer));

        int result = planningService.runPlanning();

        assertEquals(0, result);
        verify(slaRecordRepository, never()).save(any());
    }

    @Test
    void testRunPlanning_NoActiveEngineers() {
        when(requestRepository.findByStatus(Status.НОВАЯ)).thenReturn(Arrays.asList(newRequest));
        when(engineerRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        int result = planningService.runPlanning();

        assertEquals(0, result);
        verify(slaRecordRepository, never()).save(any());
    }

    @Test
    void testRunPlanning_Success() {
        when(requestRepository.findByStatus(Status.НОВАЯ)).thenReturn(Arrays.asList(newRequest));
        when(engineerRepository.findByActiveTrue()).thenReturn(Arrays.asList(engineer));
        when(slaRecordRepository.findByRequestIdAndActualEndIsNull(1)).thenReturn(Collections.emptyList());
        when(slaRecordRepository.findByEngineerIdAndActualEndIsNullOrderByPlannedEndDesc(1))
                .thenReturn(Collections.emptyList());
        when(slaRecordRepository.countByEngineerIdAndRequestStatus(1, Status.В_РАБОТЕ)).thenReturn(0);
        when(requestRepository.save(any(Request.class))).thenReturn(newRequest);
        when(slaRecordRepository.save(any(SlaRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int result = planningService.runPlanning();

        assertEquals(1, result);
        verify(slaRecordRepository, times(1)).save(any(SlaRecord.class));
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testRunPlanning_RequestAlreadyAssigned() {
        SlaRecord existingSla = new SlaRecord();
        existingSla.setId(1);

        when(requestRepository.findByStatus(Status.НОВАЯ)).thenReturn(Arrays.asList(newRequest));
        when(engineerRepository.findByActiveTrue()).thenReturn(Arrays.asList(engineer));
        when(slaRecordRepository.findByRequestIdAndActualEndIsNull(1)).thenReturn(Arrays.asList(existingSla));

        int result = planningService.runPlanning();

        assertEquals(0, result);
        verify(slaRecordRepository, never()).save(any());
    }

    @Test
    void testRunPlanning_EngineerSelection() {
        // Проверяем косвенно через планирование
        Request criticalRequest = new Request();
        criticalRequest.setId(2);
        criticalRequest.setPriority(Priority.КРИТИЧЕСКИЙ);
        criticalRequest.setStatus(Status.НОВАЯ);

        Engineer level3Engineer = new Engineer();
        level3Engineer.setId(2);
        level3Engineer.setFio("Петров П.П.");
        level3Engineer.setLineLevel(LineLevel.THREE);
        level3Engineer.setActive(true);

        when(requestRepository.findByStatus(Status.НОВАЯ)).thenReturn(Arrays.asList(criticalRequest));
        when(engineerRepository.findByActiveTrue()).thenReturn(Arrays.asList(level3Engineer));
        when(slaRecordRepository.findByRequestIdAndActualEndIsNull(2)).thenReturn(Collections.emptyList());
        when(slaRecordRepository.findByEngineerIdAndActualEndIsNullOrderByPlannedEndDesc(2))
                .thenReturn(Collections.emptyList());
        when(requestRepository.save(any(Request.class))).thenReturn(criticalRequest);
        when(slaRecordRepository.save(any(SlaRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int result = planningService.runPlanning();

        assertEquals(1, result);
    }
}