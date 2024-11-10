package school.faang.user_service.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.RequestStatusDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Log4j2
@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    public MentorshipRequest generateMentorshipRequest() {
        User user1 = new User();
        User user2 = new User();
        user1.setId(1L);
        user1.setMentors(new ArrayList<>());
        user1.setMentees(new ArrayList<>());
        user2.setId(2L);
        user2.setMentors(new ArrayList<>());
        user2.setMentees(new ArrayList<>());
        return new MentorshipRequest(1L, "Description", user1, user2,
                RequestStatus.PENDING, null, LocalDateTime.now(), null);

    }


    @Test
    void createRequestMentorshipSuccessTest() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto("Request description", 2L, 3L);

        when(userRepository.existsById(2L)).thenReturn(true);
        when(userRepository.existsById(3L)).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(2L, 3L)).thenReturn(Optional.empty());

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        verify(mentorshipRequestRepository).create(2L, 3L, "Request description");
    }

    @Test
    void createRequestMentorshipForLastRequestDateLess3MonthsFailTest() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto("Request description", 2L, 3L);
        MentorshipRequest lastRequest = new MentorshipRequest();
        lastRequest.setCreatedAt(LocalDateTime.now().minusMonths(2));

        when(userRepository.existsById(2L)).thenReturn(true);
        when(userRepository.existsById(3L)).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(2L, 3L)).thenReturn(Optional.of(lastRequest));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        assertTrue(exception.getMessage().contains("Last request has date less 3 months"));// + lastRequest.get().getCreatedAt()));
    }

    @Test
    void createRequestMentorshipForNonExistantRequesterIdFailTest() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto("Request description", 2L, 3L);

        when(userRepository.existsById(2L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        assertTrue(exception.getMessage().contains("Requester id=2 not found"));
    }

    @Test
    void getRequestFiltersAppliedSuccessTest() {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setDescription("desc");
        User user1 = new User();
        User user2 = new User();
        user1.setId(1L);
        user2.setId(2L);
        mentorshipRequest.setRequester(user1);
        mentorshipRequest.setReceiver(user2);
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(mentorshipRequest));
        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(new MentorshipRequestDto("desc", 2L, 3L));

        List<MentorshipRequestDto> result = mentorshipRequestService.getRequest(null, 1L, 2L, RequestStatusDto.PENDING);

        assertEquals(1, result.size());
        assertEquals("desc", result.get(0).description());
    }

    @Test
    void acceptRequestRequestExistsSuccessTest() {
        MentorshipRequest mentorshipRequest = generateMentorshipRequest();

        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        mentorshipRequestService.acceptRequest(1L);

        assertTrue(requester.getMentors().contains(receiver));
        assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
    }

    @Test
    void acceptRequestRequestNotExistsFailTest() {
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(1L));

        assertTrue(exception.getMessage().contains("Request with id: 1 does not exist"));
    }

    @Test
    void rejectRequestRequestExistsSuccessTest() {
        MentorshipRequest mentorshipRequest = new MentorshipRequest();

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.rejectRequest(1L, "Not interested");

        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals("Not interested", mentorshipRequest.getRejectionReason());
    }

    @Test
    void rejectRequestRequestNotExistsShouldFailTest() {
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.rejectRequest(1L, "Not interested"));

        assertTrue(exception.getMessage().contains("Request with id: 1 does not exist"));
    }
}