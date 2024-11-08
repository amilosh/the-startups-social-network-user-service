package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.dto.skill.SkillCandidateDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @GetMapping("/{skillId}/users/{userId}")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId, @PathVariable long userId) {
        log.info("Recived request to acquire skill with ID {} for user ID {}", skillId, userId);
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    @GetMapping("/user/{userId}/offered-skills")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId) {
        log.info("Received request to get offered skills for user ID {} ", userId);
        return skillService.getOfferedSkills(userId);
    }

    @GetMapping("/user/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        log.info("Received request to get user skills for for user ID {} ", userId);
        return skillService.getUserSkills(userId);
    }

    @PostMapping("/create")
    public SkillDto create(@RequestBody SkillDto skill) {
        log.info("Received request to create skill with title '{}'", skill.getTitle());
        validateSkill(skill);
        return skillService.create(skill);
    }

    @GetMapping("/all")
    public List<SkillDto> getAllSkills() {
        log.info(" Received request to get all skills ");
        return skillService.getAllSkills();
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().trim().isEmpty()) {
            log.warn(" Validation failed: Skill title cannot be empty ");
            throw new DataValidationException("Skill title cannot be empty ");
        }
    }
}
