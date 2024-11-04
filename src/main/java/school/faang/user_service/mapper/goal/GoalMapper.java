package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(source = "parent.id",target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "skillsToSkillIds")
    GoalDto toDto(Goal goal);

    @Mapping(target = "parent", ignore = true)
    @Mapping(source = "skillIds", target = "skillsToAchieve", qualifiedByName = "skillIdsToSkills")
    Goal toEntity(GoalDto goalDto);

    List<GoalDto> toDto(List<Goal> goals);

    @Named("skillsToSkillIds")
    default List<Long> skillsToSkillIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }

    @Named("skillIdsToSkills")
    default List<Skill> skillIdsToSkills(List<Long> skillIds) {
        return skillIds.stream()
                .map(id -> {
                    Skill skill = new Skill();
                    skill.setId(id);
                    return skill;
                })
                .collect(Collectors.toList());
    }
}