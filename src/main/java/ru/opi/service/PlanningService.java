package ru.opi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.opi.model.Engineer;
import ru.opi.model.Request;
import ru.opi.model.SlaRecord;
import ru.opi.model.Status;
import ru.opi.model.Priority;
import ru.opi.model.LineLevel;
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
        List<Request> pendingRequests = requestRepository.findByStatus(Status.НОВАЯ);
        List<Engineer> activeEngineers = engineerRepository.findByActiveTrue();

        if (pendingRequests.isEmpty() || activeEngineers.isEmpty()) {
            return 0;
        }

        int assignedCount = 0;

        for (Request request : pendingRequests) {
            int requiredLevel = getRequiredLevel(request.getPriority());
            List<Engineer> candidates = activeEngineers.stream()
                    .filter(e -> e.getLineLevel().getValue() >= requiredLevel)
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

                request.setStatus(Status.В_РАБОТЕ);
                requestRepository.save(request);

                assignedCount++;
            }
        }
        return assignedCount;
    }

    /**
     * Определяет требуемый уровень инженера на основе приоритета заявки
     */
    private int getRequiredLevel(Priority priority) {
        if (priority == Priority.КРИТИЧЕСКИЙ || priority == Priority.ВЫСОКИЙ) return 3;
        if (priority == Priority.СРЕДНИЙ) return 2;
        return 1;
    }

    /**
     * Возвращает текущую загрузку инженера (количество заявок в работе)
     */
    private int getEngineerWorkload(Engineer engineer) {
        return slaRecordRepository.countByEngineerIdAndRequestStatus(engineer.getId(), Status.В_РАБОТЕ);
    }
}