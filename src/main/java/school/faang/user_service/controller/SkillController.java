package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping
    public SkillDto create(@RequestBody SkillDto skill) {
        return skillService.create(skill);
    }

    @GetMapping("/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping
    public List<SkillCandidateDto> getOfferedSkills(@RequestParam long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PutMapping
    public Optional<SkillDto> acquireSkillFromOffers(@RequestParam long skillId, @RequestParam long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }


}
