package school.faang.user_service.controller.skill;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/skills")
@Tag(name = "Skill Controller", description = "Controller for managing skills")
@ApiResponse(responseCode = "201", description = "Skill created successfully")
@ApiResponse(responseCode = "400", description = "Invalid request data")
@ApiResponse(responseCode = "404", description = "Data not found")
@ApiResponse(responseCode = "500", description = "Internal server error")
public class SkillController {
    private final SkillService service;

    @Operation(
            summary = "Create a new skill",
            description = "Add a new skill to the system"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@Valid @RequestBody SkillDto skillDto) {
        return service.create(skillDto);
    }

    @Operation(
            summary = "Get user skills",
            description = "Retrieve all skills associated with a specific user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Skills retrieved successfully")
            }
    )
    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<SkillDto> getUserSkills(
            @PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return service.getUserSkills(userId);
    }

    @Operation(
            summary = "Get offered skills",
            description = "Retrieve a list of skills offered to a specific user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Offered skills retrieved successfully")
            }
    )
    @GetMapping("/users/{userId}/offered-skills")
    @ResponseStatus(HttpStatus.OK)
    public List<SkillCandidateDto> getOfferedSkills(
            @PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return service.getOfferedSkills(userId);
    }

    @Operation(
            summary = "Acquire a skill from offers",
            description = "Allow a user to acquire a skill from their offered skills list",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Skill acquired successfully")
            }
    )
    @PostMapping("/users/{userId}/acquire/{skillId}")
    @ResponseStatus(HttpStatus.OK)
    public SkillDto acquireSkillFromOffers(
            @PathVariable @NotNull(message = "Skill ID should not be null") Long skillId,
            @PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return service.acquireSkillFromOffers(skillId, userId);
    }
}
