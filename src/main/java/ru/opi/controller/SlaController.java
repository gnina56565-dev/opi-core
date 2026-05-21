package ru.opi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.opi.model.SlaRecord;
import ru.opi.service.PlanningService;
import ru.opi.service.SlaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sla")
@RequiredArgsConstructor
public class SlaController {

    private final SlaService slaService;
    private final PlanningService planningService;

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
}