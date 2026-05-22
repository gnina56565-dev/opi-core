package ru.opi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.opi.model.Competence;
import ru.opi.model.Priority;
import ru.opi.model.Request;
import ru.opi.model.RequestDto;
import ru.opi.model.Status;
import ru.opi.repository.CompetenceRepository;
import ru.opi.service.RequestService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
class RequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestService requestService;

    @MockBean
    private CompetenceRepository competenceRepository;

    private Request testRequest;
    private RequestDto testDto;
    private Competence testCompetence;

    @BeforeEach
    void setUp() {
        testCompetence = new Competence();
        testCompetence.setId(1);
        testCompetence.setName("Тестовая компетенция");

        testRequest = new Request();
        testRequest.setId(1);
        testRequest.setSubject("Тестовая заявка");
        testRequest.setSpecification("Описание");
        testRequest.setContactInfo("test@example.com");
        testRequest.setPriority(Priority.СРЕДНИЙ);
        testRequest.setStatus(Status.НОВАЯ);
        testRequest.setCreatedAt(LocalDateTime.now());
        testRequest.setSlaDeadline(LocalDateTime.now().plusHours(24));
        testRequest.setCompetence(testCompetence);

        testDto = RequestDto.fromEntity(testRequest);
    }

    @Test
    void testGetAllRequests() throws Exception {
        when(requestService.findAll()).thenReturn(Arrays.asList(testRequest));

        mockMvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].subject").value("Тестовая заявка"));

        verify(requestService, times(1)).findAll();
    }

    @Test
    void testGetRequest_Success() throws Exception {
        when(requestService.findById(1)).thenReturn(testRequest);

        mockMvc.perform(get("/api/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("Тестовая заявка"));

        verify(requestService, times(1)).findById(1);
    }

    @Test
    void testGetRequest_NotFound() throws Exception {
        when(requestService.findById(999)).thenThrow(new RequestService.ResourceNotFoundException("Не найдено"));

        mockMvc.perform(get("/api/requests/999"))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).findById(999);
    }

    @Test
    void testCreateRequest_Success() throws Exception {
        when(competenceRepository.findById(1)).thenReturn(Optional.of(testCompetence));
        when(requestService.create(any(Request.class))).thenReturn(testRequest);

        String requestJson = objectMapper.writeValueAsString(testDto);

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subject").value("Тестовая заявка"));

        verify(requestService, times(1)).create(any(Request.class));
    }

    @Test
    void testCreateRequest_NullCompetence() throws Exception {
        testDto.setIdCompetence(null);
        String requestJson = objectMapper.writeValueAsString(testDto);

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).create(any());
    }

    @Test
    void testUpdateRequest_Success() throws Exception {
        when(requestService.findById(1)).thenReturn(testRequest);
        when(competenceRepository.findById(1)).thenReturn(Optional.of(testCompetence));
        when(requestService.update(eq(1), any(Request.class))).thenReturn(testRequest);

        String requestJson = objectMapper.writeValueAsString(testDto);

        mockMvc.perform(put("/api/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("Тестовая заявка"));

        verify(requestService, times(1)).update(eq(1), any(Request.class));
    }

    @Test
    void testDeleteRequest_Success() throws Exception {
        doNothing().when(requestService).delete(1);

        mockMvc.perform(delete("/api/requests/1"))
                .andExpect(status().isNoContent());

        verify(requestService, times(1)).delete(1);
    }

    @Test
    void testDeleteRequest_NotFound() throws Exception {
        doThrow(new RequestService.ResourceNotFoundException("Не найдено")).when(requestService).delete(999);

        mockMvc.perform(delete("/api/requests/999"))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).delete(999);
    }

    @Test
    void testEscalateRequest_Success() throws Exception {
        testRequest.setStatus(Status.ЭСКАЛИРОВАНА);
        testRequest.setEscalationReason("Причина эскалации");
        when(requestService.escalate(1, "Причина эскалации")).thenReturn(testRequest);

        mockMvc.perform(patch("/api/requests/1/escalate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Причина эскалации\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ЭСКАЛИРОВАНА"));

        verify(requestService, times(1)).escalate(eq(1), anyString());
    }
}