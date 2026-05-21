package ru.opi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.opi.model.Engineer;
import ru.opi.model.SlaRecord;
import ru.opi.service.PlanningService;
import ru.opi.service.SlaService;
import ru.opi.repository.EngineerRepository;
import ru.opi.repository.SlaRecordRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sla")
@RequiredArgsConstructor
public class SlaController {

    private final SlaService slaService;
    private final PlanningService planningService;
    private final EngineerRepository engineerRepository;
    private final SlaRecordRepository slaRecordRepository;

    @GetMapping
    public ResponseEntity<List<SlaRecord>> getAllSla() {
        return ResponseEntity.ok(slaService.findAll());
    }

    @PostMapping("/planning/run")
    public ResponseEntity<Map<String, Object>> runPlanning() {
        int count = planningService.runPlanning();
        return ResponseEntity.ok(Map.of(
                "message", "Алгоритм планирования выполнен успешно",
                "assignedCount", count
        ));
    }

    @GetMapping("/engineers")
    public ResponseEntity<List<Engineer>> getAllEngineers() {
        return ResponseEntity.ok(engineerRepository.findAll());
    }

    @PostMapping("/engineers")
    public ResponseEntity<Engineer> createEngineer(@RequestBody Engineer engineer) {
        if (engineer.getFio() == null || engineer.getFio().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (engineer.getLineLevel() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        Engineer saved = engineerRepository.save(engineer);
        return ResponseEntity.status(201).body(saved);
    }

    @DeleteMapping("/engineers/{id}")
    public ResponseEntity<Void> deleteEngineer(@PathVariable Integer id) {
        try {
            // Сначала удаляем связанные записи SLA, чтобы избежать нарушения внешнего ключа
            List<SlaRecord> slaRecords = slaRecordRepository.findAll().stream()
                    .filter(sla -> sla.getEngineer().getId().equals(id))
                    .collect(java.util.stream.Collectors.toList());
            if (!slaRecords.isEmpty()) {
                slaRecordRepository.deleteAll(slaRecords);
            }

            engineerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}