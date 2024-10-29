package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;  // Import ArrayList
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    private MentorshipRequest mentorshipRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1L);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
    }

    @Test
    void testRequestMentorship_LessThan3Days() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setId(1L);

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1L);
        mentorshipRequest.setCreatedAt(LocalDateTime.now().minusDays(2));
        mentorshipRequest.setRequester(new User());
        mentorshipRequest.setReceiver(new User());

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        assertEquals("You can't request mentorship for less than 3 days", exception.getMessage());
    }

    @Test
    void testGetRequests_Filtered() {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        requestFilterDto.setStatus(RequestStatus.PENDING);
        requestFilterDto.setRequesterId(1L);
        requestFilterDto.setDescription("Test Description");

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        User requester = new User();
        requester.setId(1L);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setDescription("Test Description");

        when(mentorshipRequestRepository.findAll()).thenReturn(Collections.singletonList(mentorshipRequest));

        List<MentorshipRequest> result = mentorshipRequestService.getRequests(requestFilterDto);

        assertEquals(1, result.size());
        assertEquals(mentorshipRequest, result.get(0));
    }


    @Test
    void testAcceptMentorship() {
        Long id = 1L;
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(id);

        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));

        Long result = mentorshipRequestService.acceptMentorship(id);

        assertEquals(id, result);
    }

    @Test
    public void testRejectRequest() {
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("No longer interested");
        mentorshipRequestService.rejectRequest(1L, rejectionDto);
        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals("No longer interested", mentorshipRequest.getRejectionReason());
        verify(mentorshipRequestRepository).save(mentorshipRequest);
    }

}
