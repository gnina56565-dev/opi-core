package ru.opi.repository;

import ru.opi.model.Engineer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EngineerRepository extends JpaRepository<Engineer, Integer> {
    List<Engineer> findByActiveTrue();
}