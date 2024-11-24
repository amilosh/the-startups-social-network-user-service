package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Spy
    private MentorshipRequestMapperImpl mentorshipRequestMapper;

    @Mock
    private List<MentorshipRequestFilter> mentorshipRequestFilters;

    @Mock
    private MentorshipRequestValidator validator;

    @Mock
    private UserService userService;

    @Test
    public void testGetMentorshipRequest() {
        mentorshipRequestService.getRequests(new MentorshipRequestFilterDto());
        verify(mentorshipRequestRepository).findAll();
    }

    @Test
    public void testCreateRequest() {
        MentorshipRequestDto dto = prepareMentorshipRequestDto(1L, 2L, "description");

        when(mentorshipRequestRepository.findLatestRequest(dto.getRequesterUserId(), dto.getReceiverUserId()))
                .thenReturn(Optional.of(new MentorshipRequest()));

        mentorshipRequestService.createRequestMentorship(dto);
        verify(mentorshipRequestRepository).save(any());
    }


    @Test
    public void testFindLatestRequest() {
        MentorshipRequestDto dto = prepareMentorshipRequestDto(1L, 2L, "description");
        Optional<MentorshipRequest> expected = Optional.of(new MentorshipRequest());

        when(mentorshipRequestRepository
                .findLatestRequest(dto.getRequesterUserId(), dto.getReceiverUserId()))
                .thenReturn(expected);

        MentorshipRequest result = mentorshipRequestService
                .getLatestRequest(dto.getRequesterUserId(), dto.getReceiverUserId()).get();

        assertEquals(expected.get(), result);
    }

    @Test
    public void testAcceptRequestNotFound() {
        long invalidId = -1;

        when(mentorshipRequestRepository.findById(invalidId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.acceptRequest(invalidId));
    }

    @Test
    public void testRequestAlreadyAccepted() {
        MentorshipRequest request = prepareDataMentorshipRequest(1L, true);
        when(mentorshipRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        assertThrows(IllegalStateException.class,
                () -> mentorshipRequestService.acceptRequest(request.getId()));
    }

    @Test
    public void testAcceptRequestSuccessful() {
        MentorshipRequest request = prepareDataMentorshipRequest(2L, false);
        when(mentorshipRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        mentorshipRequestService.acceptRequest(request.getId());
    }

    @Test
    public void testRejectRequestNotFound() {
        testAcceptRequestNotFound();
    }

    @Test
    public void testRejectRequestSuccessful() {
        MentorshipRequest request = prepareDataMentorshipRequest(2L, false);
        when(mentorshipRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        mentorshipRequestService.rejectRequest(request.getId(), new RejectionDto());
    }

    private MentorshipRequest prepareDataMentorshipRequest(long id, boolean isAccepted) {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(id);
        User requesterUser = new User();
        User receiverUser = new User();
        if (isAccepted) {
            requesterUser.setMentors(List.of(receiverUser));
        } else {
            requesterUser.setMentors(new ArrayList<>());
        }
        request.setRequester(requesterUser);
        request.setReceiver(receiverUser);
        return request;
    }

    private MentorshipRequestDto prepareMentorshipRequestDto(long requesterId, long receiverId, String description) {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterUserId(requesterId);
        dto.setReceiverUserId(receiverId);
        dto.setDescription(description);

        return dto;
    }
}
