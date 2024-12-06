package school.faang.user_service.validator.mentorship_request;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceValidationTest {
    @InjectMocks
    private MentorshipRequestValidation validation;
    @Mock
    private UserRepository repository;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    private final long NON_EXIST_USER_ID = 123456L;
    private final long receiverId = 1L;
    private final long requestId = 1L;
    private User requester;
    private User receiver;
    private MentorshipRequest mentorshipRequest;

    @BeforeEach
    void initData() {
        mentorshipRequest = MentorshipRequest.builder()
                .status(RequestStatus.ACCEPTED)
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();
        requester = User.builder()
                .id(requestId)
                .username("Denis")
                .email("denis@mail.ru")
                .sentMentorshipRequests(Collections.singletonList(mentorshipRequest))
                .build();
        receiver = User.builder()
                .id(receiverId)
                .username("Max")
                .email("max@mail.ru")
                .mentees(new ArrayList<>(Collections.singletonList(requester)))
                .build();
    }

    @Test
    public void testValidateIdWithNonExistentUserId() {
        when(repository.findById(NON_EXIST_USER_ID)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> validation.validateId(NON_EXIST_USER_ID));
    }

    @Test
    public void testValidateSameId() {
        assertThrows(IllegalArgumentException.class, () -> validation.validateSameId(receiverId, requestId));
    }

    @Test
    public void testValidate3MonthsFromTheLastRequest() {
        when(mentorshipRequestRepository.findLatestRequest(requestId, receiverId))
                .thenReturn(Optional.ofNullable(mentorshipRequest));
        assertThrows(IllegalArgumentException.class,
                () -> validation.validate3MonthsFromTheLastRequest(requester, receiver));
    }

    @Test
    public void testValidateRequestIdWithNonExistentRequestId() {
        when(mentorshipRequestRepository.getReferenceById(requestId)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> validation.validateRequestId(requestId));
    }

    @Test
    public void testValidateRequestIdWithExistentRequestId() {
        when(mentorshipRequestRepository.getReferenceById(1L)).thenReturn(mentorshipRequest);
        assertEquals(mentorshipRequest, validation.validateRequestId(requestId));
    }

    @Test
    public void testValidateOfBeingInMentorship() {
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        assertThrows(IllegalArgumentException.class, () -> validation.validateOfBeingInMentorship(mentorshipRequest));
    }

    @Test
    public void testValidateStatusIsAccepted() {
        assertThrows(IllegalArgumentException.class, () -> validation.validateStatus(mentorshipRequest));
    }
}
