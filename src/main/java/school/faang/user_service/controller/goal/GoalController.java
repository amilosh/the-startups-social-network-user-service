package school.faang.user_service.controller.goal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.entity.goal.Goal;

@RestController
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;

    // Внедрение зависимости GoalService с помощью конструктора
    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    /**
     * Метод, обрабатывающий создание новой цели для пользователя
     */
    @PostMapping("/create")
    public String createGoal(@RequestParam Long userId, @RequestBody Goal goal) {
        // Проверка на наличие названия цели
        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            return "Error: Title is required.";
        }

        // Вызов метода createGoal в GoalService и обработка результата
        Optional<Goal> createdGoal = goalService.createGoal(userId, goal);
        return createdGoal.isPresent() ? "Goal created successfully." : "Error: Goal could not be created.";
    }
}
