package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/skills")
public class SkillController {
    private final SkillService service;

    @PostMapping
    public SkillDto create(@Valid @RequestBody SkillDto skillDto) {
        return service.create(skillDto);
    }

    @GetMapping("/users/{userId}")
    public List<SkillDto> getUserSkills(
            @PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return service.getUserSkills(userId);
    }

    @GetMapping("/users/{userId}/offered-skills")
    public List<SkillCandidateDto> getOfferedSkills(
            @PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return service.getOfferedSkills(userId);
    }

    @PostMapping("/users/{userId}/acquire/{skillId}")
    public SkillDto acquireSkillFromOffers(
            @PathVariable @NotNull(message = "Skill ID should not be null") Long skillId,
            @PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return service.acquireSkillFromOffers(skillId, userId);
    }
}
