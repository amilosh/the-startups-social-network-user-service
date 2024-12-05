package school.faang.user_service.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.validator.GoalValidator.MAX_USER_GOALS_SIZE;

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {

    @Mock
    private GoalRepository goalRepository;

    @Spy
    @InjectMocks
    private GoalValidator validator;

    long parentId;
    List<Long> skillToAchieveIds;
    GoalDto goalDto;
    long userId;
    Optional<User> user;
    Optional<Goal> parentGoal;
    List<Skill> skills;
    long goalId;
    Optional<Goal> goal;

    @BeforeEach
    void setUp() {
        parentId = 1L;
        skillToAchieveIds = List.of(1L, 2L);
        goalDto = GoalDto.builder()
                .parentId(parentId)
                .skillToAchieveIds(skillToAchieveIds)
                .build();
        userId = 1L;
        user = Optional.of(new User());
        parentGoal = Optional.of(new Goal());
        skills = List.of();

        goalId = 2L;
        goal = Optional.of(new Goal());
    }

    @Test
    public void testValidateCreate_Successfully() {
        doNothing().when(validator).validateUser(anyLong(), any(Optional.class));
        doNothing().when(validator).validateTitle(any(GoalDto.class));
        doNothing().when(validator).validateDescription(any(GoalDto.class));
        doNothing().when(validator).validateParent(anyLong(), any(Optional.class));
        doNothing().when(validator).validateSkills(anyList(), anyList());

        validator.validateCreate(goalDto, userId, user, parentGoal, skills);

        verify(validator, times(1)).validateUser(userId, user);
        verify(validator, times(1)).validateTitle(goalDto);
        verify(validator, times(1)).validateDescription(goalDto);
        verify(validator, times(1)).validateParent(parentId, parentGoal);
        verify(validator, times(1)).validateSkills(skillToAchieveIds, skills);
    }

    @Test
    public void testValidateCreate_NotValidUser() {
        doThrow(new IllegalArgumentException()).when(validator).validateUser(anyLong(), any(Optional.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateCreate(goalDto, userId, user, parentGoal, skills));
    }

    @Test
    public void testValidateCreate_NotValidTitle() {
        doNothing().when(validator).validateUser(anyLong(), any(Optional.class));
        doThrow(new IllegalArgumentException()).when(validator).validateTitle(any(GoalDto.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateCreate(goalDto, userId, user, parentGoal, skills));
    }

    @Test
    public void testValidateCreate_NotValidDescription() {
        doNothing().when(validator).validateUser(anyLong(), any(Optional.class));
        doNothing().when(validator).validateTitle(any(GoalDto.class));
        doThrow(new IllegalArgumentException()).when(validator).validateDescription(any(GoalDto.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateCreate(goalDto, userId, user, parentGoal, skills));
    }

    @Test
    public void testValidateCreate_NotValidParent() {
        doNothing().when(validator).validateUser(anyLong(), any(Optional.class));
        doNothing().when(validator).validateTitle(any(GoalDto.class));
        doNothing().when(validator).validateDescription(any(GoalDto.class));
        doThrow(new IllegalArgumentException()).when(validator).validateParent(anyLong(), any(Optional.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateCreate(goalDto, userId, user, parentGoal, skills));
    }

    @Test
    public void testValidateCreate_NotValidSkills() {
        doNothing().when(validator).validateUser(anyLong(), any(Optional.class));
        doNothing().when(validator).validateTitle(any(GoalDto.class));
        doNothing().when(validator).validateDescription(any(GoalDto.class));
        doNothing().when(validator).validateParent(anyLong(), any(Optional.class));
        doThrow(new IllegalArgumentException()).when(validator).validateSkills(anyList(), anyList());

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateCreate(goalDto, userId, user, parentGoal, skills));
    }

    @Test
    public void testValidateUpdate_Successfully() {
        doNothing().when(validator).validateGoalOnUpdate(anyLong(), any(Optional.class));
        doNothing().when(validator).validateTitle(any(GoalDto.class));
        doNothing().when(validator).validateDescription(any(GoalDto.class));
        doNothing().when(validator).validateParent(anyLong(), any(Optional.class));
        doNothing().when(validator).validateSkills(anyList(), anyList());

        validator.validateUpdate(goalId, goal, goalDto, parentGoal, skills);

        verify(validator, times(1)).validateGoalOnUpdate(goalId, goal);
        verify(validator, times(1)).validateTitle(goalDto);
        verify(validator, times(1)).validateDescription(goalDto);
        verify(validator, times(1)).validateParent(parentId, parentGoal);
        verify(validator, times(1)).validateSkills(skillToAchieveIds, skills);
    }

    @Test
    public void testValidateUpdate_NotValidGoal() {
        doThrow(new IllegalArgumentException()).when(validator).validateGoalOnUpdate(anyLong(), any(Optional.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateUpdate(goalId, goal, goalDto, parentGoal, skills));
    }

    @Test
    public void testValidateUpdate_NotValidTitle() {
        doNothing().when(validator).validateGoalOnUpdate(anyLong(), any(Optional.class));
        doThrow(new IllegalArgumentException()).when(validator).validateTitle(any(GoalDto.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateUpdate(goalId, goal, goalDto, parentGoal, skills));
    }

    @Test
    public void testValidateUpdate_NotValidDescription() {
        doNothing().when(validator).validateGoalOnUpdate(anyLong(), any(Optional.class));
        doNothing().when(validator).validateTitle(any(GoalDto.class));
        doThrow(new IllegalArgumentException()).when(validator).validateDescription(any(GoalDto.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateUpdate(goalId, goal, goalDto, parentGoal, skills));
    }

    @Test
    public void testValidateUpdate_NotValidParent() {
        doNothing().when(validator).validateGoalOnUpdate(anyLong(), any(Optional.class));
        doNothing().when(validator).validateTitle(any(GoalDto.class));
        doNothing().when(validator).validateDescription(any(GoalDto.class));
        doThrow(new IllegalArgumentException()).when(validator).validateParent(anyLong(), any(Optional.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateUpdate(goalId, goal, goalDto, parentGoal, skills));
    }

    @Test
    public void testValidateUpdate_NotValidSkills() {
        doNothing().when(validator).validateGoalOnUpdate(anyLong(), any(Optional.class));
        doNothing().when(validator).validateTitle(any(GoalDto.class));
        doNothing().when(validator).validateDescription(any(GoalDto.class));
        doNothing().when(validator).validateParent(anyLong(), any(Optional.class));
        doThrow(new IllegalArgumentException()).when(validator).validateSkills(anyList(), anyList());

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateUpdate(goalId, goal, goalDto, parentGoal, skills));
    }

    @Test
    public void testValidateUser_Successfully() {
        List<Goal> goals = List.of();
        User user = User.builder().goals(goals).build();
        Optional<User> userOpt = Optional.of(user);

        validator.validateUser(userId, userOpt);
    }

    @Test
    public void testValidateUser_UserNotExist() {
        long userId = 1L;
        Optional<User> userOpt = Optional.empty();

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateUser(userId, userOpt));
    }

    @Test
    public void testValidateUser_TooManyGoals() {
        List<Goal> goals = Stream.generate(Goal::new).limit(MAX_USER_GOALS_SIZE).toList();
        Optional<User> userOpt = Optional.of(User.builder().goals(goals).build());

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateUser(userId, userOpt));
    }

    @Test
    public void testValidateGoalOnUpdate_Successfully() {
        validator.validateGoalOnUpdate(goalId, goal);
    }

    @Test
    public void testValidateGoalOnUpdate_GoalNotExist() {
        goal = Optional.empty();
        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateGoalOnUpdate(goalId, goal));
    }

    @Test
    public void testValidateGoalOnUpdate_GoalAlreadyCompleted() {
        goal = Optional.of(Goal.builder().status(GoalStatus.COMPLETED).build());
        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateGoalOnUpdate(goalId, goal));
    }

    @Test
    public void testValidateTitle_Successfully() {
        goalDto.setTitle("123");
        when(goalRepository.existsGoalByTitle(anyString())).thenReturn(false);

        validator.validateTitle(goalDto);
    }

    @Test
    public void testValidateTitle_TitleAlreadyExist() {
        goalDto.setTitle("123");
        when(goalRepository.existsGoalByTitle(anyString())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateTitle(goalDto));
    }

    @Test
    public void testValidateDescription_Successfully() {
        goalDto.setDescription("123");
        when(goalRepository.existsGoalByDescription(anyString())).thenReturn(false);

        validator.validateDescription(goalDto);
    }

    @Test
    public void testValidateDescription_DescriptionAlreadyExist() {
        goalDto.setDescription("123");
        when(goalRepository.existsGoalByDescription(anyString())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateDescription(goalDto));
    }

    @Test
    public void testValidateParent_Successfully() {
        validator.validateParent(parentId, parentGoal);
    }

    @Test
    public void testValidateParent_ParentNotExist() {
        parentGoal = Optional.empty();

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateParent(parentId, parentGoal));
    }

    @Test
    public void testValidateSkills_Successfully() {
        validator.validateSkills(skillToAchieveIds, skills);

        verify(validator).checkSkillsExistingByIds(skillToAchieveIds, skills);
    }

    @Test
    public void testCheckSkillsExistingByIds_Successfully() {
        Skill firstSkill = Skill.builder().id(1L).build();
        Skill secondSkill = Skill.builder().id(2L).build();
        skills = List.of(firstSkill, secondSkill);

        validator.checkSkillsExistingByIds(skillToAchieveIds, skills);
    }

    @Test
    public void testValidateParent_SkillNotExist() {
        Skill firstSkill = Skill.builder().id(1L).build();
        skills = List.of(firstSkill);

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.checkSkillsExistingByIds(skillToAchieveIds, skills));
    }
}
