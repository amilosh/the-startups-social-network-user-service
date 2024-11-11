package school.faang.user_service.validator.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.abstracts.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestValidatorImplTest {
    private static final Long REQUEST_ID = 1L;
    private static final Long REQUESTER_ID = 1L;
    private static final Long RECEIVER_ID = 2L;
    private static final int MONTHS_UNTIL_NEXT_REQUEST = 3;

    @Mock
    private UserService userService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @InjectMocks
    private MentorshipRequestValidatorImpl mentorshipRequestValidator;

    @Test
    public void testValidationOfSameRequesterAndReceiverIds() {
        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.validateRequesterAndReceiver(REQUESTER_ID, REQUESTER_ID));

        assertEquals("requester and receiver must be different", thrown.getMessage());
    }

    @Test
    public void testValidationWhenRequesterDoesntExist() {
        when(userService.existsById(REQUESTER_ID)).thenReturn(false);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.validateRequesterAndReceiver(REQUESTER_ID, RECEIVER_ID));

        verify(userService).existsById(REQUESTER_ID);
        assertEquals("requester doesn't exist", thrown.getMessage());
    }

    @Test
    public void testValidationWhenReceiverDoesntExist() {
        when(userService.existsById(REQUESTER_ID)).thenReturn(true);
        when(userService.existsById(RECEIVER_ID)).thenReturn(false);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.validateRequesterAndReceiver(REQUESTER_ID, RECEIVER_ID));

        verify(userService).existsById(RECEIVER_ID);
        assertEquals("receiver doesn't exist", thrown.getMessage());
    }

    @Test
    public void testValidationOfRequestInterval() {
        String invalidIntervalException = String.format("you can send a request to one " +
                "receiver once every %d months", MONTHS_UNTIL_NEXT_REQUEST);
        LocalDateTime elapsedInterval = LocalDateTime.now().minusMonths(MONTHS_UNTIL_NEXT_REQUEST - 1);
        MentorshipRequest requestEntity = MentorshipRequest.builder().createdAt(elapsedInterval).build();
        when(mentorshipRequestRepository.findLatestRequest(REQUESTER_ID, RECEIVER_ID))
                .thenReturn(Optional.of(requestEntity));

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.validateRequestInterval(REQUESTER_ID, RECEIVER_ID));

        verify(mentorshipRequestRepository).findLatestRequest(REQUESTER_ID, RECEIVER_ID);
        assertEquals(invalidIntervalException, thrown.getMessage());
    }

    @Test
    public void testGetDoesntExistRequest() {
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.getRequestByIdOrThrowException(REQUEST_ID));

        verify(mentorshipRequestRepository).findById(REQUEST_ID);
        assertEquals("request doesn't exist", thrown.getMessage());
    }

    @Test
    public void testValidationOfRequestStatus() {
        MentorshipRequest requestEntity = MentorshipRequest.builder().status(RequestStatus.ACCEPTED).build();

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> mentorshipRequestValidator.validateRequestStatus(requestEntity, RequestStatus.ACCEPTED));

        assertEquals("the request has already been accepted", thrown.getMessage());
    }
}