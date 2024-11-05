package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class GoalMapperTest {
    private GoalMapper goalMapper;

    @BeforeEach
    public void setUp() {
        goalMapper = Mappers.getMapper(GoalMapper.class);
    }

    @Test
    public void testToDto_MappingSuccess() {
        Goal parentGoal = Goal.builder().id(100L).build();

        Skill firstSkill = Skill.builder().id(1L).build();
        Skill secondSkill = Skill.builder().id(2L).build();

        Goal goal = Goal.builder()
                .id(1L)
                .title("Goal Title")
                .description("Goal Description")
                .parent(parentGoal)
                .skillsToAchieve(List.of(firstSkill, secondSkill))
                .build();

        GoalDto goalDTO = goalMapper.toDto(goal);

        assertEquals(goal.getId(), goalDTO.getId());
        assertEquals(goal.getTitle(), goalDTO.getTitle());
        assertEquals(goal.getDescription(), goalDTO.getDescription());
        assertEquals(parentGoal.getId(), goalDTO.getParentGoalId());
        assertEquals(List.of(firstSkill.getId(), secondSkill.getId()), goalDTO.getSkillIds());
    }

    @Test
    public void testToEntityMappingSuccess() {
        GoalDto goalDTO = GoalDto.builder()
                .id(1L)
                .title("Goal Title")
                .description("Goal Description")
                .parentGoalId(100L)
                .skillIds(List.of(1L, 2L))
                .build();

        Goal goal = goalMapper.toEntity(goalDTO);

        assertEquals(goalDTO.getId(), goal.getId());
        assertEquals(goalDTO.getTitle(), goal.getTitle());
        assertEquals(goalDTO.getDescription(), goal.getDescription());
    }
}