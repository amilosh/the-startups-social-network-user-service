package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalValidator goalValidator;

    @Mock
    private GoalMapperImpl goalMapper;

    @Mock
    private List<GoalFilter> goalFilters;

    @InjectMocks
    private GoalService goalService;

    private GoalDto goalDto;
    private Goal goal;
    private GoalFilterDto filterDto;

    @BeforeEach
    void setUp() {
        goalDto = new GoalDto();
        goalDto.setTitle("New Goal");
        goalDto.setDescription("Description");
        goalDto.setParentId(1L);
        goalDto.setSkillIds(List.of(1L, 2L));

        goal = new Goal();
        goal.setId(1L);
        goal.setTitle("New Goal");

        filterDto = new GoalFilterDto();
    }

    @Test
    void testCreateGoalSuccessful() {
        when(goalRepository.create("New Goal", "Description", 1L)).thenReturn(goal);
        when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDto);

        GoalDto result = goalService.createGoal(1L, goalDto);

        assertEquals(goalDto, result);
        verify(goalValidator).validateCreationGoal(1L, goalDto);
        verify(goalRepository).create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        verify(goalRepository, times(goalDto.getSkillIds().size())).addSkillToGoal(eq(goal.getId()), anyLong());

    }

    @Test
    void testCreateGoalValidationException() {
        doThrow(new DataValidationException("Invalid data")).when(goalValidator)
                .validateCreationGoal(anyLong(), any(GoalDto.class));

        assertThrows(DataValidationException.class, () -> goalService.createGoal(1L, goalDto));
        verify(goalValidator).validateCreationGoal(1L, goalDto);
        verify(goalRepository, never()).create(anyString(), anyString(), anyLong());
    }

    @Test
    void testUpdateGoalSuccessful() {
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
        when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDto);
        when(goalMapper.toEntity(any(GoalDto.class))).thenReturn(goal);

        GoalDto result = goalService.updateGoal(1L, goalDto);

        assertEquals(goalDto, result);
        verify(goalValidator).validateUpdatingGoal(1L, goalDto);
        verify(goalRepository).save(goal);
    }

    @Test
    void testUpdateGoalNotFoundException() {
        when(goalRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> goalService.updateGoal(1L, goalDto));
        verify(goalRepository).findById(1L);
        verify(goalValidator, never()).validateUpdatingGoal(anyLong(), any(GoalDto.class));
    }

    @Test
    void testDeleteGoal() {
        goalService.deleteGoal(1L);

        verify(goalRepository).deleteById(1L);
    }

    @Test
    void testFindSubtasksByGoalId() {
        GoalFilter mockFilter = mock(GoalFilter.class);
        when(goalFilters.stream()).thenReturn(Stream.of(mockFilter));
        when(mockFilter.isApplicable(any(GoalFilterDto.class))).thenReturn(true);
        when(mockFilter.apply(any(Stream.class), any(GoalFilterDto.class))).thenReturn(Stream.of(goal));
        when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDto);

        List<GoalDto> result = goalService.findSubtasksByGoalId(1L, filterDto);

        assertEquals(1, result.size());
        assertEquals(goalDto, result.get(0));
        verify(mockFilter).isApplicable(filterDto);
        verify(mockFilter).apply(any(Stream.class), eq(filterDto));
    }

    @Test
    void testGetGoalsByUser() {
        GoalFilter mockFilter = mock(GoalFilter.class);
        when(goalFilters.stream()).thenReturn(Stream.of(mockFilter));
        when(mockFilter.isApplicable(any(GoalFilterDto.class))).thenReturn(true);
        when(mockFilter.apply(any(Stream.class), any(GoalFilterDto.class))).thenReturn(Stream.of(goal));
        when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDto);

        List<GoalDto> result = goalService.getGoalsByUser(1L, filterDto);

        assertEquals(1, result.size());
        verify(mockFilter).isApplicable(filterDto);
        verify(mockFilter).apply(any(Stream.class), eq(filterDto));
    }
}