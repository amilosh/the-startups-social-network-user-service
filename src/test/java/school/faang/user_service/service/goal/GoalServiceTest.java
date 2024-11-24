package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.mapper.goal.GoalRequestMapper;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalValidator goalValidator;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private GoalRequestMapper goalRequestMapper;

    @Mock
    private List<GoalFilter> goalFilters;

    @InjectMocks
    private GoalService goalService;

    private GoalRequestDto goalRequestDto;
    private GoalResponseDto goalResponseDto;
    private Goal goal;
    private GoalFilterDto filterDto;

    @BeforeEach
    void setUp() {
        goalRequestDto = new GoalRequestDto();
        goalRequestDto.setTitle("New Goal");
        goalRequestDto.setDescription("Description");
        goalRequestDto.setParentId(1L);
        goalRequestDto.setSkillIds(List.of(1L, 2L));

        goalResponseDto = new GoalResponseDto();
        goalResponseDto.setTitle("New Goal");
        goalResponseDto.setDescription("Description");
        goalResponseDto.setParentId(1L);
        goalResponseDto.setSkillIds(List.of(1L, 2L));

        goal = new Goal();
        goal.setId(1L);
        goal.setTitle("New Goal");

        filterDto = new GoalFilterDto();
    }

    @Test
    void testCreateGoalSuccessful() {
        when(goalRepository.create(goalRequestDto.getTitle(), goalRequestDto.getDescription(), goalRequestDto.getParentId()))
                .thenReturn(goal);
        when(goalMapper.toDto(goal)).thenReturn(goalResponseDto);

        GoalResponseDto result = goalService.createGoal(1L, goalRequestDto);

        assertEquals(goalResponseDto, result);
        verify(goalValidator).validateCreationGoal(1L, goalRequestDto);
        verify(goalRepository).create(goalRequestDto.getTitle(), goalRequestDto.getDescription(), goalRequestDto.getParentId());
        verify(goalRepository, times(goalRequestDto.getSkillIds().size()))
                .addSkillToGoal(eq(goal.getId()), anyLong());
    }

    @Test
    void testCreateGoalValidationException() {
        doThrow(new DataValidationException("Invalid data")).when(goalValidator)
                .validateCreationGoal(anyLong(), any(GoalRequestDto.class));

        assertThrows(DataValidationException.class, () -> goalService.createGoal(1L, goalRequestDto));
        verify(goalValidator).validateCreationGoal(1L, goalRequestDto);
        verify(goalRepository, never()).create(any(), any(), any());
    }

    @Test
    void testUpdateGoalSuccessful() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalMapper.toDto(any(Goal.class))).thenReturn(goalResponseDto);
        when(goalRequestMapper.toEntity(goalRequestDto)).thenReturn(goal);

        GoalResponseDto result = goalService.updateGoal(1L, goalRequestDto);

        assertEquals(goalResponseDto, result);
        verify(goalValidator).validateUpdatingGoal(1L, goalRequestDto);
        verify(goalRepository).removeSkillsFromGoal(goal.getId());
        verify(goalRepository, times(goalRequestDto.getSkillIds().size()))
                .addSkillToGoal(anyLong(), eq(goal.getId()));
        verify(goalRepository).save(goal);
    }

    @Test
    void testUpdateGoalNotFoundException() {
        when(goalRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> goalService.updateGoal(1L, goalRequestDto));
        verify(goalRepository).findById(1L);
        verify(goalValidator, never()).validateUpdatingGoal(anyLong(), any());
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
        when(mockFilter.isApplicable(filterDto)).thenReturn(true);
        when(mockFilter.apply(any(Stream.class), eq(filterDto))).thenReturn(Stream.of(goal));
        when(goalMapper.toDto(goal)).thenReturn(goalResponseDto);

        List<GoalResponseDto> result = goalService.findSubtasksByGoalId(1L, filterDto);

        assertEquals(1, result.size());
        assertEquals(goalResponseDto, result.get(0));
        verify(mockFilter).isApplicable(filterDto);
        verify(mockFilter).apply(any(Stream.class), eq(filterDto));
    }

    @Test
    void testGetGoalsByUser() {
        GoalFilter mockFilter = mock(GoalFilter.class);
        when(goalFilters.stream()).thenReturn(Stream.of(mockFilter));
        when(mockFilter.isApplicable(filterDto)).thenReturn(true);
        when(mockFilter.apply(any(Stream.class), eq(filterDto))).thenReturn(Stream.of(goal));
        when(goalMapper.toDto(goal)).thenReturn(goalResponseDto);

        List<GoalResponseDto> result = goalService.getGoalsByUser(1L, filterDto);

        assertEquals(1, result.size());
        verify(mockFilter).isApplicable(filterDto);
        verify(mockFilter).apply(any(Stream.class), eq(filterDto));
    }
}

