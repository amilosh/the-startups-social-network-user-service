package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Collectors;

public interface GoalRequestMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "skillsToSkillIds")
    GoalRequestDto toDto(Goal goal);

    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toEntity(GoalRequestDto goalDto);

    List<GoalRequestDto> toDto(List<Goal> goals);

    @Named("skillsToSkillIds")
    default List<Long> skillsToSkillIds(List<Skill> skills) {
        return skills == null ? List.of() : skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}