package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.ControllerNotValidatesSkillException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillController {
    @Autowired
    private SkillService skillService;

    public SkillDto create(SkillDto skill) {
        if (!validateSkill(skill)) {
            throw new ControllerNotValidatesSkillException("the skill has not a title") ;
        }

        return skillService.create(skill);
    }

    private boolean validateSkill(SkillDto skill) {
        if (skill.getTitle().isEmpty() || skill.getTitle().isBlank()) {
            return false;
        }
        return true;
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    @GetMapping("/any")
    public void getRequest(@RequestBody SkillDto skilldto) {

    }

    @PostMapping("/any")
    public void postRequest(@RequestBody SkillDto skilldto) {

    }

    @PutMapping("/any")
    public void putRequest(@RequestBody SkillDto skilldto) {

    }

    @DeleteMapping("/any")
    public void deleteRequest(@RequestBody SkillDto skilldto) {

    }

}
