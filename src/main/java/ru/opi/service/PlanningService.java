package ru.opi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.opi.model.Engineer;
import ru.opi.model.Request;
import ru.opi.model.SlaRecord;
import ru.opi.repository.EngineerRepository;
import ru.opi.repository.RequestRepository;
import ru.opi.repository.SlaRecordRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanningService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private EngineerRepository engineerRepository;

    @Autowired
    private SlaRecordRepository slaRecordRepository;

    @Transactional
    public int runPlanning() {
        List<Request> pendingRequests = requestRepository.findByStatus("новая");
        List<Engineer> activeEngineers = engineerRepository.findByActiveTrue();

        if (pendingRequests.isEmpty() || activeEngineers.isEmpty()) {
            return 0;
        }

        int assignedCount = 0;

        for (Request request : pendingRequests) {
            int requiredLevel = getRequiredLevel(request.getPriority());

            List<Engineer> candidates = activeEngineers.stream()
                    .filter(e -> e.getLineLevel() >= requiredLevel)
                    .sorted(Comparator.comparingInt(this::getEngineerWorkload))
                    .collect(Collectors.toList());

            if (!candidates.isEmpty()) {
                Engineer bestEngineer = candidates.get(0);

                SlaRecord sla = new SlaRecord();
                sla.setRequest(request);
                sla.setEngineer(bestEngineer);
                sla.setPlannedStart(LocalDateTime.now());
                sla.setPlannedEnd(LocalDateTime.now().plusHours(4));
                sla.setSlaForecast(true);

                slaRecordRepository.save(sla);

                request.setStatus("в работе");
                requestRepository.save(request);

                assignedCount++;
            }
        }
        return assignedCount;
    }

    private int getRequiredLevel(String priority) {
        if ("критический".equals(priority) || "высокий".equals(priority)) return 3;
        if ("средний".equals(priority)) return 2;
        return 1;
    }

    private int getEngineerWorkload(Engineer engineer) {
        return slaRecordRepository.countByEngineerIdAndRequestStatus(engineer.getIdEngineer(), "в работе");
    }
}