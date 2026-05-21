package ru.opi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.opi.model.Competence;
import ru.opi.model.Engineer;
import ru.opi.model.LineLevel;
import ru.opi.repository.CompetenceRepository;
import ru.opi.repository.EngineerRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CompetenceRepository competenceRepository, EngineerRepository engineerRepository) {
        return args -> {
            if (competenceRepository.count() == 0) {
                competenceRepository.save(new Competence(1, "1 линия поддержки", "Первичная обработка заявок"));
                competenceRepository.save(new Competence(2, "2 линия поддержки", "Углубленная техническая поддержка"));
                competenceRepository.save(new Competence(3, "Сетевые технологии", "Администрирование сетей"));
                competenceRepository.save(new Competence(4, "Базы данных", "Администрирование СУБД"));
                competenceRepository.save(new Competence(5, "Веб-приложения", "Поддержка веб-систем"));
                System.out.println("Компетенции инициализированы");
            }

            if (engineerRepository.count() == 0) {
                Engineer e1 = new Engineer();
                e1.setId(1);
                e1.setFio("Иванов Иван Иванович");
                e1.setLineLevel(LineLevel.ONE);
                e1.setActive(true);

                Engineer e2 = new Engineer();
                e2.setId(2);
                e2.setFio("Петров Петр Петрович");
                e2.setLineLevel(LineLevel.TWO);
                e2.setActive(true);

                Engineer e3 = new Engineer();
                e3.setId(3);
                e3.setFio("Сидоров Сидор Сидорович");
                e3.setLineLevel(LineLevel.THREE);
                e3.setActive(true);

                engineerRepository.save(e1);
                engineerRepository.save(e2);
                engineerRepository.save(e3);
                System.out.println("Инженеры инициализированы");
            }
        };
    }
}