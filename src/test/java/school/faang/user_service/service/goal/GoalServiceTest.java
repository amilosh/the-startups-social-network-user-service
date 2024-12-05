package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalValidator goalValidator;
    @Spy
    private GoalMapper goalMapper = new GoalMapperImpl();
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @InjectMocks
    private GoalService goalService;

    @Nested
    class TestCreate {

        long userId;
        User userFromDb;
        Optional<User> userFromDbOpt;

        long parentId;
        Goal parentGoalFromDb;
        Optional<Goal> parentGoalFromDbOpt;

        List<Long> skillToAchieveIds;
        GoalDto goalDto;

        Skill firstSkillFromDb;
        Skill secondSkillFromDb;
        List<Skill> skillsToAchieveFromDb;

        @BeforeEach
        public void setup() {
            userId = 1L;
            userFromDb = User.builder()
                    .id(userId)
                    .build();
            userFromDbOpt = Optional.of(userFromDb);

            parentId = 1L;
            parentGoalFromDb = Goal.builder()
                    .id(parentId)
                    .build();
            parentGoalFromDbOpt = Optional.of(parentGoalFromDb);

            skillToAchieveIds = List.of(1L, 2L);
            firstSkillFromDb = Skill.builder().id(1L).build();
            secondSkillFromDb = Skill.builder().id(2L).build();
            skillsToAchieveFromDb = List.of(firstSkillFromDb, secondSkillFromDb);

            goalDto = GoalDto.builder()
                    .parentId(parentId)
                    .skillToAchieveIds(skillToAchieveIds)
                    .build();
        }

        @Test
        public void testCreateGoal_Successfully() {
            Goal savedGoal = Goal.builder().id(2L).build();
            ArgumentCaptor<Goal> goalArgumentCaptor = ArgumentCaptor.forClass(Goal.class);
            when(userService.findById(userId)).thenReturn(userFromDbOpt);
            when(goalRepository.findById(parentId)).thenReturn(parentGoalFromDbOpt);
            when(skillService.findByIdIn(skillToAchieveIds)).thenReturn(skillsToAchieveFromDb);
            when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);

            goalService.create(userId, goalDto);

            verify(goalValidator, times(1)).validateCreate(any(GoalDto.class), eq(userId), eq(userFromDbOpt), eq(parentGoalFromDbOpt), eq(skillsToAchieveFromDb));
            verify(goalRepository, times(1)).save(goalArgumentCaptor.capture());
            Goal goal = goalArgumentCaptor.getValue();
            assertEquals(1, goal.getUsers().size());
            assertEquals(userId, goal.getUsers().get(0).getId());
            assertEquals(parentGoalFromDb, goal.getParent());
            assertEquals(skillsToAchieveFromDb, goal.getSkillsToAchieve());
            assertEquals(GoalStatus.ACTIVE, goal.getStatus());
            verify(goalMapper, times(1)).toDto(savedGoal);
        }

        @Test
        public void testCreate_NotValidData() {
            when(userService.findById(userId)).thenReturn(userFromDbOpt);
            when(goalRepository.findById(parentId)).thenReturn(parentGoalFromDbOpt);
            when(skillService.findByIdIn(skillToAchieveIds)).thenReturn(skillsToAchieveFromDb);
            doThrow(IllegalArgumentException.class).when(goalValidator).validateCreate(goalDto, userId, userFromDbOpt, parentGoalFromDbOpt, skillsToAchieveFromDb);

            Assertions.assertThrows(IllegalArgumentException.class, () -> goalService.create(userId, goalDto));

            verify(goalValidator, times(1)).validateCreate(goalDto, userId, userFromDbOpt, parentGoalFromDbOpt, skillsToAchieveFromDb);
        }
    }

    @Nested
    class TestUpdate {

        long goalId;
        Goal goalFromDb;
        Optional<Goal> goalFromDbOpt;

        long parentId;
        Goal parentGoalFromDb;
        Optional<Goal> parentGoalFromDbOpt;

        List<Long> skillToAchieveIds;
        GoalDto goalDto;

        Skill firstSkillFromDb;
        Skill secondSkillFromDb;
        List<Skill> skillsToAchieveFromDb;

        @BeforeEach
        public void setup() {
            goalId = 2L;
            goalFromDb = Goal.builder()
                    .id(goalId)
                    .build();
            goalFromDbOpt = Optional.of(goalFromDb);

            parentId = 1L;
            parentGoalFromDb = Goal.builder()
                    .id(parentId)
                    .build();
            parentGoalFromDbOpt = Optional.of(parentGoalFromDb);

            skillToAchieveIds = List.of(1L, 2L);
            firstSkillFromDb = Skill.builder().id(1L).build();
            secondSkillFromDb = Skill.builder().id(2L).build();
            skillsToAchieveFromDb = List.of(firstSkillFromDb, secondSkillFromDb);

            goalDto = GoalDto.builder()
                    .parentId(parentId)
                    .skillToAchieveIds(skillToAchieveIds)
                    .build();
        }

        @Test
        public void testUpdateGoal_Successfully() {
            ArgumentCaptor<Goal> goalArgumentCaptor = ArgumentCaptor.forClass(Goal.class);
            when(goalRepository.findById(goalId)).thenReturn(goalFromDbOpt);
            when(goalRepository.findById(parentId)).thenReturn(parentGoalFromDbOpt);
            when(skillService.findByIdIn(skillToAchieveIds)).thenReturn(skillsToAchieveFromDb);

            goalService.update(goalId, goalDto);

            verify(goalValidator, times(1)).validateUpdate(goalId, goalFromDbOpt, goalDto, parentGoalFromDbOpt, skillsToAchieveFromDb);
            verify(goalRepository, times(1)).save(goalArgumentCaptor.capture());
            Goal goal = goalArgumentCaptor.getValue();
            assertEquals(parentGoalFromDb, goal.getParent());
            assertEquals(skillsToAchieveFromDb, goal.getSkillsToAchieve());
        }

        @Test
        public void testUpdate_GoalStatusChangeFromActiveToCompleted() {
            Skill thirdSkill = Skill.builder().id(2L).build();

            User firstUserFromDb = User.builder()
                    .id(1L)
                    .skills(new ArrayList<>(List.of(firstSkillFromDb, thirdSkill)))
                    .build();
            User secondUserFromDb = User.builder()
                    .id(2L)
                    .skills(new ArrayList<>())
                    .build();
            List<User> usersFromDb = List.of(firstUserFromDb, secondUserFromDb);

            List<Long> skillToAchieveIds = List.of(1L, 2L);
            GoalDto goalDto = GoalDto.builder()
                    .skillToAchieveIds(skillToAchieveIds)
                    .status(GoalStatus.COMPLETED.toString())
                    .build();

            goalFromDb.setStatus(GoalStatus.ACTIVE);
            goalFromDb.setUsers(usersFromDb);

            ArgumentCaptor<Goal> goalArgumentCaptor = ArgumentCaptor.forClass(Goal.class);
            when(goalRepository.findById(goalId)).thenReturn(goalFromDbOpt);
            when(skillService.findByIdIn(skillToAchieveIds)).thenReturn(skillsToAchieveFromDb);

            goalService.update(goalId, goalDto);

            verify(goalRepository, times(1)).save(goalArgumentCaptor.capture());
            Goal goal = goalArgumentCaptor.getValue();
            assertIterableEquals(usersFromDb, goal.getUsers());
            assertThat(usersFromDb).hasSameElementsAs(goal.getUsers());
            goal.getUsers().forEach(user -> assertTrue(user.getSkills().containsAll(skillsToAchieveFromDb)));
            assertEquals(skillsToAchieveFromDb, goal.getSkillsToAchieve());
        }

        @Test
        public void testUpdate_NotValidData() {
            when(goalRepository.findById(goalId)).thenReturn(goalFromDbOpt);
            when(goalRepository.findById(parentId)).thenReturn(parentGoalFromDbOpt);
            when(skillService.findByIdIn(skillToAchieveIds)).thenReturn(skillsToAchieveFromDb);
            doThrow(IllegalArgumentException.class).when(goalValidator).validateUpdate(anyLong(), any(Optional.class), any(GoalDto.class), any(Optional.class), anyList());

            Assertions.assertThrows(IllegalArgumentException.class, () -> goalService.update(goalId, goalDto));

            verify(goalValidator, times(1)).validateUpdate(goalId, goalFromDbOpt, goalDto, parentGoalFromDbOpt, skillsToAchieveFromDb);
        }
    }

    @Test
    public void testDelete_Successfully() {
        long goalId = 1L;

        goalRepository.deleteById(goalId);

        verify(goalRepository, times(1)).deleteById(goalId);
    }
}