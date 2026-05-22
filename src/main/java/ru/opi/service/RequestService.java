package ru.opi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.opi.model.Engineer;
import ru.opi.model.Request;
import ru.opi.model.Status;
import ru.opi.model.Priority;
import ru.opi.repository.RequestRepository;
import ru.opi.repository.EngineerRepository;

import java.util.List;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private EngineerRepository engineerRepository;

    @Autowired
    private PlanningService planningService;

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public List<Request> getActiveRequestsByEngineer(Engineer engineer) {
        return requestRepository.findByEngineerAndStatusIn(engineer, List.of(Status.НОВАЯ, Status.В_РАБОТЕ));
    }

    @Transactional
    public Request createRequest(String title, String description, String priorityStr) {
        Priority priority = Priority.valueOf(priorityStr);

        Request request = new Request();
        request.setSubject(title); // Используем setSubject вместо setTitle, судя по вашему DTO
        request.setSpecification(description);
        request.setPriority(priority);
        request.setStatus(Status.НОВАЯ);

        // Сохраняем, но не назначаем инженера сразу - это сделает планировщик
        return requestRepository.save(request);
    }

    @Transactional
    public void completeRequest(Integer id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка не найдена: " + id));

        request.setStatus(Status.ЗАВЕРШЕНА);
        request.setActualEnd(java.time.LocalDateTime.now());
        requestRepository.save(request);
    }

    @Transactional
    public void distributePending() {
        planningService.runPlanning();
    }

    // Внутренний класс исключения
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}