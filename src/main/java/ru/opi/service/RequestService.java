package ru.opi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.opi.model.Request;
import ru.opi.model.Status;
import ru.opi.model.Priority;
import ru.opi.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    public List<Request> findAll() {
        return requestRepository.findAll();
    }

    public Request findById(Integer id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка с ID " + id + " не найдена"));
    }

    public Request create(Request request) {
        if (request.getSubject() == null || request.getSubject().isEmpty()) {
            throw new IllegalArgumentException("Тема заявки обязательна");
        }
        if (request.getPriority() == null) {
            throw new IllegalArgumentException("Приоритет обязателен");
        }

        request.setStatus(Status.НОВАЯ);
        request.setCreatedAt(LocalDateTime.now());

        // Установка SLA deadline на основе приоритета
        int slaHours = getSlaHours(request.getPriority());
        request.setSlaDeadline(LocalDateTime.now().plusHours(slaHours));

        return requestRepository.save(request);
    }

    private int getSlaHours(Priority priority) {
        switch (priority) {
            case КРИТИЧЕСКИЙ: return 4;
            case ВЫСОКИЙ: return 8;
            case СРЕДНИЙ: return 24;
            case НИЗКИЙ: return 72;
            default: return 24;
        }
    }

    public Request update(Integer id, Request updatedRequest) {
        Request existing = findById(id);

        if (updatedRequest.getSubject() != null) existing.setSubject(updatedRequest.getSubject());
        if (updatedRequest.getSpecification() != null) existing.setSpecification(updatedRequest.getSpecification());
        if (updatedRequest.getPriority() != null) existing.setPriority(updatedRequest.getPriority());

        if (updatedRequest.getStatus() != null) {
            existing.setStatus(updatedRequest.getStatus());
        }

        if (updatedRequest.getEscalationReason() != null) {
            existing.setEscalationReason(updatedRequest.getEscalationReason());
        }

        return requestRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!requestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Заявка с ID " + id + " не найдена");
        }
        requestRepository.deleteById(id);
    }

    public Request escalate(Integer id, String reason) {
        Request request = findById(id);
        request.setStatus(Status.ЭСКАЛИРОВАНА);
        request.setEscalationReason(reason);
        return requestRepository.save(request);
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}