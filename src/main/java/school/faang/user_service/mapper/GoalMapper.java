package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface GoalMapper {
    /**
     * Maps a {@link Goal} entity to a {@link GoalDto} DTO.
     *
     * @param goal the entity to map
     * @return the mapped DTO
     */
    @Mapping(target = "skillIds", expression = "java(goal.getSkillsToAchieve().stream().map(skill -> skill.getId()).toList())")
    @Mapping(target = "parentGoalId", expression = "java(goal.getParent() != null ? goal.getParent().getId() : null)")
    GoalDto toDto(Goal goal);

    /**
     * Maps a {@link GoalDto} to a {@link Goal} entity.
     *
     * @param goalDTO the DTO to map
     * @return the mapped entity
     */
    Goal toEntity(GoalDto goalDTO);
}
