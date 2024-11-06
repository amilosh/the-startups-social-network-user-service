package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/skills")  // Базовый URL для контроллера
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping
    public SkillDto create(@Valid @RequestBody SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @GetMapping("/users/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable @NotNull @Positive long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/users/{userId}/offers")
    public List<SkillDto> getOfferedSkills(@PathVariable @NotNull@Positive long userId) {
        return skillService.getOfferedSkills(userId);
    }
}
