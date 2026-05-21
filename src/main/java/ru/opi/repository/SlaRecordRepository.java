package ru.opi.repository;

import ru.opi.model.SlaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlaRecordRepository extends JpaRepository<SlaRecord, Integer> {
    int countByEngineerIdAndRequestStatus(Integer engineerId, ru.opi.model.Status status);
    List<SlaRecord> findByRequestIdAndActualEndIsNull(Integer requestId);
    List<SlaRecord> findByEngineerIdAndActualEndIsNullOrderByPlannedEndDesc(Integer engineerId);
}