package ru.opi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.opi.model.Priority;
import ru.opi.model.Request;
import ru.opi.model.Status;
import ru.opi.repository.RequestRepository;
import ru.opi.repository.SlaRecordRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private SlaRecordRepository slaRecordRepository;

    @InjectMocks
    private RequestService requestService;

    private Request testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new Request();
        testRequest.setId(1);
        testRequest.setSubject("Тестовая заявка");
        testRequest.setSpecification("Описание тестовой заявки");
        testRequest.setContactInfo("test@example.com");
        testRequest.setPriority(Priority.СРЕДНИЙ);
        testRequest.setStatus(Status.НОВАЯ);
        testRequest.setCreatedAt(LocalDateTime.now());
        testRequest.setSlaDeadline(LocalDateTime.now().plusHours(24));
    }

    @Test
    void testFindAll() {
        List<Request> requests = Arrays.asList(testRequest);
        when(requestRepository.findAll()).thenReturn(requests);

        List<Request> result = requestService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Тестовая заявка", result.get(0).getSubject());
        verify(requestRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Success() {
        when(requestRepository.findById(1)).thenReturn(Optional.of(testRequest));

        Request result = requestService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(requestRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_NotFound() {
        when(requestRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> requestService.findById(999));
        verify(requestRepository, times(1)).findById(999);
    }

    @Test
    void testCreate_Success() {
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        Request result = requestService.create(testRequest);

        assertNotNull(result);
        assertEquals(Status.НОВАЯ, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getSlaDeadline());
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testCreate_EmptySubject() {
        testRequest.setSubject("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> requestService.create(testRequest));
        assertEquals("Тема заявки обязательна", exception.getMessage());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void testCreate_NullPriority() {
        testRequest.setPriority(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> requestService.create(testRequest));
        assertEquals("Приоритет обязателен", exception.getMessage());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void testUpdate_Success() {
        Request updatedRequest = new Request();
        updatedRequest.setSubject("Обновленная тема");
        updatedRequest.setPriority(Priority.ВЫСОКИЙ);

        when(requestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        Request result = requestService.update(1, updatedRequest);

        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testDelete_Success() {
        when(requestRepository.existsById(1)).thenReturn(true);
        when(slaRecordRepository.findByRequestIdAndActualEndIsNull(1)).thenReturn(Arrays.asList());

        requestService.delete(1);

        verify(requestRepository, times(1)).deleteById(1);
    }

    @Test
    void testDelete_NotFound() {
        when(requestRepository.existsById(999)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> requestService.delete(999));
        verify(requestRepository, never()).deleteById(any());
    }

    @Test
    void testEscalate_Success() {
        when(requestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        Request result = requestService.escalate(1, "Причина эскалации");

        assertEquals(Status.ЭСКАЛИРОВАНА, result.getStatus());
        assertEquals("Причина эскалации", result.getEscalationReason());
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testGetSlaHours_ForCriticalPriority() {
        testRequest.setPriority(Priority.КРИТИЧЕСКИЙ);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        Request result = requestService.create(testRequest);

        assertNotNull(result.getSlaDeadline());
    }

    @Test
    void testGetSlaHours_ForLowPriority() {
        testRequest.setPriority(Priority.НИЗКИЙ);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        Request result = requestService.create(testRequest);

        assertNotNull(result.getSlaDeadline());
    }
}