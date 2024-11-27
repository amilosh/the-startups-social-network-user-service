package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "skillsToSkillIds")
    GoalResponseDto toDto(Goal goal);

    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toEntity(GoalRequestDto goalDto);

    List<GoalResponseDto> toDto(List<Goal> goals);

    @Named("skillsToSkillIds")
    default List<Long> skillsToSkillIds(List<Skill> skills) {
        return skills == null ? List.of() : skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}