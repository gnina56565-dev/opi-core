package ru.opi.service;

import ru.opi.model.SlaRecord;
import ru.opi.repository.SlaRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlaService {

    @Autowired
    private SlaRecordRepository slaRecordRepository;

    public void saveSla(SlaRecord sla) {
        slaRecordRepository.save(sla);
    }

    public List<SlaRecord> findAll() {
        return slaRecordRepository.findAll();
    }
}