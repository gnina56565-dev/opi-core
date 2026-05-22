package ru.opi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.opi.model.Engineer;
import ru.opi.model.LineLevel;
import ru.opi.repository.EngineerRepository;

import java.util.List;

@Service
public class EngineerService {

    @Autowired
    private EngineerRepository engineerRepository;

    public List<Engineer> getAllEngineers() {
        return engineerRepository.findAll();
    }

    public List<Engineer> getActiveEngineers() {
        return engineerRepository.findByActiveTrue();
    }

    public Engineer getEngineerById(Long id) {
        return engineerRepository.findById(id).orElse(null);
    }

    @Transactional
    public Engineer createEngineer(String name, String specialization, LineLevel level) {
        Engineer engineer = new Engineer();
        engineer.setName(name);
        engineer.setSpecialization(specialization);
        engineer.setLevel(level != null ? level : LineLevel.FIRST); // Уровень по умолчанию
        engineer.setActive(true);
        return engineerRepository.save(engineer);
    }

    @Transactional
    public Engineer createEngineer(String name, String specialization) {
        return createEngineer(name, specialization, null);
    }

    @Transactional
    public void deactivateEngineer(Long id) {
        Engineer engineer = engineerRepository.findById(id).orElseThrow();
        engineer.setActive(false);
        engineerRepository.save(engineer);
    }
}