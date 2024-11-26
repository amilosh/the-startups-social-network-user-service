package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillAcquireDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;


@Validated
@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<SkillDto> create(@Valid @RequestBody SkillDto skillDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.create(skillDto));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<SkillDto>> getUserSkills(@PathVariable @Positive Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(skillService.getUserSkills(userId));
    }

    @GetMapping("/users/{userId}/offered-skills")
    public ResponseEntity<List<SkillDto>> getOfferedSkills(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(skillService.getOfferedSkills(userId));
    }

    @PostMapping("{skillId}/users/{userId}/acquire-skill-from-offers")
    public SkillDto acquireSkillFromOffers(@PathVariable @Positive Long skillId, @PathVariable @Positive Long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
