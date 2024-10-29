package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    Goal dtoToEntity(GoalDto goalDto);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(target = "skillIds", expression = "java(skillsToIds(goal.getSkillsToAchieve()))")
    GoalDto entityToDto(Goal goal);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget Goal goal, GoalDto goalDto);

    default List<Long> skillsToIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }
}
