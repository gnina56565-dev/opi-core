package ru.opi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.opi.model.Request;
import ru.opi.model.RequestDto;
import ru.opi.model.Competence;
import ru.opi.service.RequestService;
import ru.opi.repository.CompetenceRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RequestController {

    private final RequestService requestService;
    private final CompetenceRepository competenceRepository;

    @GetMapping
    public ResponseEntity<List<RequestDto>> getAllRequests() {
        List<Request> requests = requestService.findAll();
        List<RequestDto> dtos = requests.stream()
                .map(RequestDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestDto> getRequest(@PathVariable Integer id) {
        try {
            Request request = requestService.findById(id);
            return ResponseEntity.ok(RequestDto.fromEntity(request));
        } catch (RequestService.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<RequestDto> createRequest(@RequestBody RequestDto dto) {
        try {
            if (dto.getIdCompetence() == null) {
                return ResponseEntity.badRequest().body(null);
            }
            Competence competence = competenceRepository.findById(dto.getIdCompetence().intValue())
                    .orElseThrow(() -> new IllegalArgumentException("Компетенция не найдена"));

            Request request = dto.toEntity(competence);
            Request created = requestService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(RequestDto.fromEntity(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequestDto> updateRequest(@PathVariable Integer id, @RequestBody RequestDto dto) {
        try {
            Request existing = requestService.findById(id);

            Competence competence = null;
            if (dto.getIdCompetence() != null) {
                competence = competenceRepository.findById(dto.getIdCompetence().intValue())
                        .orElseThrow(() -> new IllegalArgumentException("Компетенция не найдена"));
            }

            dto.updateEntity(existing, competence);
            Request updated = requestService.update(id, existing);
            return ResponseEntity.ok(RequestDto.fromEntity(updated));
        } catch (RequestService.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Integer id) {
        try {
            requestService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RequestService.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{id}/escalate")
    public ResponseEntity<RequestDto> escalateRequest(@PathVariable Integer id, @RequestBody String reason) {
        try {
            Request updated = requestService.escalate(id, reason);
            return ResponseEntity.ok(RequestDto.fromEntity(updated));
        } catch (RequestService.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}