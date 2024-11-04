package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDTO;
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
        Goal parentGoal = new Goal();
        parentGoal.setId(100L);

        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(2L);

        Goal goal = new Goal();
        goal.setId(1L);
        goal.setTitle("Goal Title");
        goal.setDescription("Goal Description");
        goal.setParent(parentGoal);
        goal.setSkillsToAchieve(List.of(skill1, skill2));

        GoalDTO goalDTO = goalMapper.toDto(goal);

        assertEquals(goal.getId(), goalDTO.getId());
        assertEquals(goal.getTitle(), goalDTO.getTitle());
        assertEquals(goal.getDescription(), goalDTO.getDescription());
        assertEquals(parentGoal.getId(), goalDTO.getParentGoalId());
        assertEquals(List.of(skill1.getId(), skill2.getId()), goalDTO.getSkillIds());
    }

    @Test
    public void testToEntityMappingSuccess() {
        GoalDTO goalDTO = new GoalDTO();
        goalDTO.setId(1L);
        goalDTO.setTitle("Goal Title");
        goalDTO.setDescription("Goal Description");
        goalDTO.setParentGoalId(100L);
        goalDTO.setSkillIds(List.of(1L, 2L));

        Goal goal = goalMapper.toEntity(goalDTO);

        assertEquals(goalDTO.getId(), goal.getId());
        assertEquals(goalDTO.getTitle(), goal.getTitle());
        assertEquals(goalDTO.getDescription(), goal.getDescription());
    }
}