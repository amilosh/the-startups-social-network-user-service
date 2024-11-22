package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateGoal() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Learn Java");
        goalDto.setDescription("Complete Java course");

        goalDto.setSkillsIds(List.of(1L));

        Goal savedGoal = new Goal();
        savedGoal.setId(1L);
        savedGoal.setTitle(goalDto.getTitle());
        savedGoal.setDescription(goalDto.getDescription());
        savedGoal.setStatus(GoalStatus.ACTIVE);
        savedGoal.setSkillsToAchieve(new ArrayList<>());

        when((skillRepository.existsById(1L))).thenReturn(true);
        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(1);

        GoalDto createGoal = goalService.createGoal(1L, goalDto);
        assertNotNull(createGoal);
        assertEquals("Learn Java", createGoal.getTitle());
        assertEquals("Complete Java course", createGoal.getDescription());
        assertEquals(1L, createGoal.getId());
    }

    @Test
    public void testUpdateGoal() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Learn Python course");
        goalDto.setDescription("Complete Python course");

        Goal existingGoal = new Goal();
        existingGoal.setId(1L);
        existingGoal.setTitle("Learn Java");
        existingGoal.setDescription("Complete Java course");
        existingGoal.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(existingGoal);
        when(skillRepository.findAllByUserId(anyLong())).thenReturn(List.of(new Skill()));

        GoalDto updateGoal = goalService.updateGoal(1L, goalDto);
        assertNotNull(updateGoal);
        assertEquals("Learn Python course", updateGoal.getTitle());
        assertEquals("Complete Python course", updateGoal.getDescription());
        assertEquals(1L, updateGoal.getId());
    }

    @Test
    public void testDeleteGoal() {
        Goal existingGoal = new Goal();
        existingGoal.setId(1L);
        existingGoal.setTitle("Learn Java");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(existingGoal);

        goalService.deleteGoal(1L);
        verify(goalRepository, times(1)).save(existingGoal);
    }

    @Test
    public void testFindSubtaskByGoalId() {
        Goal subtask1 = new Goal();
        subtask1.setTitle("Subtask 1");
        subtask1.setStatus(GoalStatus.ACTIVE);

        Goal subtask2 = new Goal();
        subtask2.setTitle("Subtask 2");
        subtask2.setStatus(GoalStatus.COMPLETED);

        when(goalRepository.findByParent(anyLong())).thenReturn(Stream.of(subtask1, subtask2));

        List<GoalDto> subtasks = goalService.findSubtasksByGoalId(1L, new GoalFilterDto());

        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
        assertEquals("Subtask 1", subtasks.get(0).getTitle());
    }

    @Test
    public void testGetGoalByUser() {
        Goal goal1 = new Goal();
        goal1.setTitle("Learn Java");
        goal1.setStatus(GoalStatus.ACTIVE);

        Goal goal2 = new Goal();
        goal2.setTitle("Learn Python");
        goal2.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(Stream.of(goal1, goal2));

        List<GoalDto> goals = goalService.getGoalsByUser(1L, new GoalFilterDto());
        assertNotNull(goals);
        assertEquals(2, goals.size());
        assertEquals("Learn Java", goals.get(0).getTitle());
    }
}

