package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@Valid @RequestBody SkillDto skillDto) {
        return service.create(skillDto);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<SkillDto> getUserSkills(
            @PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return service.getUserSkills(userId);
    }

    @GetMapping("/users/{userId}/offered-skills")
    @ResponseStatus(HttpStatus.OK)
    public List<SkillCandidateDto> getOfferedSkills(
            @PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return service.getOfferedSkills(userId);
    }

    @PostMapping("/users/{userId}/acquire/{skillId}")
    @ResponseStatus(HttpStatus.OK)
    public SkillDto acquireSkillFromOffers(
            @PathVariable @NotNull(message = "Skill ID should not be null") Long skillId,
            @PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return service.acquireSkillFromOffers(skillId, userId);
    }
}
