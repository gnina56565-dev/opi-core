package ru.opi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.opi.model.Engineer;
import ru.opi.model.Request;
import ru.opi.model.SlaRecord;
import ru.opi.service.EngineerService;
import ru.opi.service.PlanningService;
import ru.opi.service.RequestService;
import ru.opi.repository.SlaRecordRepository;
import ru.opi.model.LineLevel;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MainController {

    @Autowired private EngineerService engineerService;
    @Autowired private RequestService requestService;
    @Autowired private PlanningService planningService;
    @Autowired private SlaRecordRepository slaRecordRepository;

    @GetMapping("/")
    public String index(Model model) {
        // Запускаем планирование перед показом страницы
        planningService.runPlanning();

        List<Engineer> engineers = engineerService.getAllEngineers();
        List<Request> requests = requestService.getAllRequests();

        model.addAttribute("engineers", engineers);
        model.addAttribute("requests", requests);

        return "index";
    }

    @PostMapping("/engineers/create")
    public String createEngineer(@RequestParam String name, @RequestParam String specialization) {
        // Создаем инженера с уровнем по умолчанию (FIRST или ONE, проверьте ваш enum)
        // Если в форме нет выбора уровня, ставим заглушку
        engineerService.createEngineer(name, specialization, LineLevel.ONE);
        return "redirect:/";
    }

    @PostMapping("/engineers/{id}/deactivate")
    public String deactivateEngineer(@PathVariable Integer id) { // Integer вместо Long
        engineerService.deactivateEngineer(id);
        return "redirect:/";
    }

    @PostMapping("/requests/create")
    public String createRequest(@RequestParam String title,
                                @RequestParam(required = false) String description,
                                @RequestParam String priority) {
        requestService.createRequest(title, description, priority);
        return "redirect:/";
    }

    @PostMapping("/requests/{id}/complete")
    public String completeRequest(@PathVariable Integer id) { // Integer вместо Long
        requestService.completeRequest(id);
        return "redirect:/";
    }

    @PostMapping("/requests/distribute")
    public String forceDistribute() {
        planningService.runPlanning();
        return "redirect:/";
    }
}