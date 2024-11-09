package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.filter.InvitationFilter;
import school.faang.user_service.validator.goal.InvitationDtoValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceImplTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalInvitationMapper goalInvitationMapper;
    @Mock
    private List<InvitationFilter> invitationFilters;
    @Mock
    private InvitationDtoValidator invitationDtoValidator;

    @InjectMocks
    private GoalInvitationService goalInvitationService;
    private GoalInvitationDto goalInvitationDtoAccept;
    private GoalInvitation goalInvitationAccept;

    private GoalInvitationDto goalInvitationDtoReject;
    private GoalInvitation goalInvitationReject;

   @BeforeEach
    public void setUp(){
       GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
       goalInvitationDto.setInviterId(1L);
       goalInvitationDto.setInvitedUserId(2L);
       goalInvitationDto.setGoalId(1L);

       GoalInvitation goalInvitation = new GoalInvitation();
       goalInvitation.setId(1L);
       User user1 = new User();
       user1.setId(1L);
       User user2 = new User();
       user2.setId(2L);
       Goal goal = new Goal();
       goal.setId(1L);
       goalInvitation.setInviter(user1);
       goalInvitation.setInvited(user2);
       goalInvitation.setGoal(goal);

       goalInvitationAccept =new GoalInvitation();
       goalInvitationAccept.setId(1L);
       goalInvitationAccept.setStatus(RequestStatus.PENDING);
       user2.setReceivedGoalInvitations(List.of(new GoalInvitation(), new GoalInvitation()));

       User invitedUser = new User();
       goalInvitationAccept.setInvited(invitedUser);

       User invited =goalInvitationAccept.getInvited();
       invited.setGoals(new ArrayList<>(List.of(new Goal())));
       invited.setReceivedGoalInvitations(new ArrayList<>(List.of(new GoalInvitation())));

       goalInvitationAccept.setGoal(new Goal());

       goalInvitationDtoAccept = new GoalInvitationDto();
       goalInvitationDtoAccept.setId(1L);
       goalInvitationDtoAccept.setStatus(RequestStatus.ACCEPTED);

       goalInvitationReject = new GoalInvitation();
       goalInvitationReject.setId(1L);
       goalInvitationReject.setStatus(RequestStatus.PENDING);

       goal = new Goal();
       goalInvitationReject.setGoal(goal);

       goalInvitationDtoReject = new GoalInvitationDto();
       goalInvitationDtoReject.setId(1L);
       goalInvitationDtoReject.setStatus(RequestStatus.REJECTED);
   }
   @Test
   public void testCreateInvitation() {
       GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
       GoalInvitation goalInvitation = new GoalInvitation();
       GoalInvitation savedInvitation = new GoalInvitation();
       GoalInvitationDto savedDto = new GoalInvitationDto();

       when(goalInvitationMapper.toEntity(goalInvitationDto)).thenReturn(goalInvitation);
       when(goalInvitationRepository.save(goalInvitation)).thenReturn(savedInvitation);
       when(goalInvitationMapper.toDto(savedInvitation)).thenReturn(savedDto);

       GoalInvitationDto result = goalInvitationService.createInvitation(goalInvitationDto);

       assertEquals(savedDto, result);

       verify(invitationDtoValidator, times(1)).validate(goalInvitationDto);
       verify(goalInvitationMapper, times(1)).toEntity(goalInvitationDto);
       verify(goalInvitationRepository, times(1)).save(goalInvitation);
       verify(goalInvitationMapper, times(1)).toDto(savedInvitation);
   }

    @Test
    public void testAcceptGoalInvitationSuccess() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitationAccept));
        when(goalInvitationRepository.save(goalInvitationAccept)).thenReturn(goalInvitationAccept);
        when(goalInvitationMapper.toDto(goalInvitationAccept)).thenReturn(goalInvitationDtoAccept);

        GoalInvitationDto result;
        result = goalInvitationService.acceptGoalInvitation(1L);

        assertNotNull(result);
        assertEquals(RequestStatus.ACCEPTED, result.getStatus());
    }

    @Test
    public void testAcceptGoalInvitationNotFound() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            goalInvitationService.acceptGoalInvitation(1L);
        });

        assertEquals("No such goal invitation with id:1", exception.getMessage());
    }

    @Test
    public void testRejectGoalInvitationSuccess() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitationReject));
        when(goalInvitationRepository.save(goalInvitationReject)).thenReturn(goalInvitationReject);
        when(goalInvitationMapper.toDto(goalInvitationReject)).thenReturn(goalInvitationDtoReject);

        GoalInvitationDto result = goalInvitationService.rejectGoalInvitation(1L);

        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED, result.getStatus());
    }

    @Test
    public void testRejectGoalInvitationNotFound() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            goalInvitationService.rejectGoalInvitation(1L);
        });

        assertEquals("No such goal invitation with id:1", exception.getMessage());
    }

    @Test
    public void testGetInvitationsByFilter() {
        InvitationFilterDto filters = new InvitationFilterDto();
        GoalInvitation goalInvitation1 = new GoalInvitation();
        GoalInvitation goalInvitation2 = new GoalInvitation();
        List<GoalInvitation> goalInvitations = List.of(goalInvitation1, goalInvitation2);

        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);

        InvitationFilter filter1 = mock(InvitationFilter.class);
        InvitationFilter filter2 = mock(InvitationFilter.class);
        when(invitationFilters.stream()).thenReturn(Stream.of(filter1, filter2));

        when(filter1.isAcceptable(filters)).thenReturn(true);
        when(filter2.isAcceptable(filters)).thenReturn(false);

        when(filter1.apply(any(), eq(filters))).thenReturn(Stream.of(goalInvitation1));

        GoalInvitationDto dto1 = new GoalInvitationDto();
        when(goalInvitationMapper.toDto(goalInvitation1)).thenReturn(dto1);

        List<GoalInvitationDto> result = goalInvitationService.getInvitationsByFilter(filters);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));

        verify(goalInvitationRepository, times(1)).findAll();
        verify(filter1, times(1)).isAcceptable(filters);
        verify(filter2, times(1)).isAcceptable(filters);
        verify(filter1, times(1)).apply(any(), eq(filters));
        verify(filter2, times(0)).apply(any(), eq(filters));
        verify(goalInvitationMapper, times(1)).toDto(goalInvitation1);
    }
}
