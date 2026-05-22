package ru.opi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.opi.model.Engineer;
import ru.opi.model.Request;
import ru.opi.model.SlaRecord;
import ru.opi.model.Status;
import ru.opi.model.Priority;
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

        // Сортировка: Критический (0) -> Низкий (3)
        pendingRequests.sort(Comparator.comparingInt(r -> r.getPriority().ordinal()));

        for (Request request : pendingRequests) {
            int requiredLevel = getRequiredLevel(request.getPriority());

            // ИСПРАВЛЕНО: getLevel() -> getLineLevel()
            List<Engineer> candidates = activeEngineers.stream()
                    .filter(e -> e.getLineLevel() != null && e.getLineLevel().getValue() >= requiredLevel)
                    .sorted(Comparator.comparingInt(this::getEngineerWorkload))
                    .collect(Collectors.toList());

            if (!candidates.isEmpty()) {
                Engineer bestEngineer = candidates.get(0);

                // Проверка на дубли SLA (ИСПРАВЛЕНО: приведение типа ID)
                List<SlaRecord> existingSla = slaRecordRepository.findByRequestIdAndActualEndIsNull(request.getId());
                if (!existingSla.isEmpty()) {
                    continue;
                }

                // ЦЕПОЧКА: Время освобождения
                LocalDateTime freeTime = getEngineerFreeTime(bestEngineer);
                int waitHours = request.getPriority().getWaitTimeHours();
                LocalDateTime startTime = freeTime.plusHours(waitHours);
                int slaHours = request.getPriority().getSlaHours();
                LocalDateTime endTime = startTime.plusHours(slaHours);

                // Сохраняем SLA запись
                SlaRecord sla = new SlaRecord();
                sla.setRequest(request);
                sla.setEngineer(bestEngineer);
                sla.setPlannedStart(startTime);
                sla.setPlannedEnd(endTime);
                sla.setSlaForecast(true);
                slaRecordRepository.save(sla);

                // Обновляем заявку
                request.setEngineer(bestEngineer);
                request.setPlannedStart(startTime);
                request.setPlannedEnd(endTime);
                request.setStatus(Status.В_РАБОТЕ);
                requestRepository.save(request);

                assignedCount++;
            }
        }
        return assignedCount;
    }

    private LocalDateTime getEngineerFreeTime(Engineer engineer) {
        // ИСПРАВЛЕНО: приведение типа ID
        List<SlaRecord> activeSla = slaRecordRepository.findByEngineerIdAndActualEndIsNullOrderByPlannedEndDesc(engineer.getId());
        if (activeSla.isEmpty()) {
            return LocalDateTime.now();
        }
        return activeSla.get(0).getPlannedEnd();
    }

    private int getRequiredLevel(Priority priority) {
        if (priority == Priority.КРИТИЧЕСКИЙ || priority == Priority.ВЫСОКИЙ) return 3;
        if (priority == Priority.СРЕДНИЙ) return 2;
        return 1;
    }

    private int getEngineerWorkload(Engineer engineer) {
        return slaRecordRepository.countByEngineerIdAndRequestStatus(engineer.getId(), Status.В_РАБОТЕ);
    }
}