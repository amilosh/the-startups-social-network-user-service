package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.TestDataCreator;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.InvalidMentorshipRejectException;
import school.faang.user_service.exception.InvalidMentorshipRequestException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestValidatorTest {
    private static final String BLANK_STRING = "   ";

    @Mock
    private MentorshipRequestRepository requestRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private User requester;
    @Mock
    private User receiver;

    @InjectMocks
    private MentorshipRequestValidator requestValidator;

    private MentorshipRequestDto requestDto;
    private RejectionDto rejectionDto;

    @BeforeEach
    void setUp() {
        requestDto = TestDataCreator.createMentorshipRequestDto(1L, 1L, 2L, RequestStatus.PENDING, "help me with java.");
        rejectionDto = TestDataCreator.createRejectionDto("Reason");
    }

    @Test
    void testValidateRequesterDoesNotExist() {
        doThrow(new EntityNotFoundException("User does not exists"))
                .when(userValidator).isUserExists(requestDto.getRequesterId());

        assertThrows(EntityNotFoundException.class, () -> requestValidator.validateMentorshipRequest(requestDto));
    }

    @Test
    void testValidateReceiverDoesNotExist() {
        doNothing().when(userValidator).isUserExists(requestDto.getRequesterId());
        doThrow(new EntityNotFoundException("User does not exist"))
                .when(userValidator).isUserExists(requestDto.getReceiverId());

        assertThrows(EntityNotFoundException.class, () -> requestValidator.validateMentorshipRequest(requestDto));
    }

    @Test
    void testValidateOneRequestPerThreeMonthsShouldThrowException() {
        MentorshipRequest request = new MentorshipRequest();
        request.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")).minusMonths(1));

        when(requestRepository.findLatestRequest(requestDto.getRequesterId(), requestDto.getReceiverId()))
                .thenReturn(Optional.of(request));

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateMentorshipRequest(requestDto));
    }

    @Test
    void testSelfRequestShouldThrowException() {
        requestDto.setReceiverId(requestDto.getRequesterId());
        MentorshipRequest request = new MentorshipRequest();
        request.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")).minusMonths(4));

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateMentorshipRequest(requestDto));
    }

    @Test
    void testValidateMentorshipRequestIsSuccessful() {
        MentorshipRequest request = new MentorshipRequest();
        request.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")).minusMonths(4));

        when(requestRepository.findLatestRequest(requestDto.getRequesterId(), requestDto.getReceiverId()))
                .thenReturn(Optional.of(request));
        doNothing().when(userValidator).isUserExists(requestDto.getRequesterId());
        doNothing().when(userValidator).isUserExists(requestDto.getReceiverId());

        assertDoesNotThrow(() -> requestValidator.validateMentorshipRequest(requestDto));
    }

    @Test
    void testBlankDescriptionShouldThrowException() {
        requestDto.setDescription(BLANK_STRING);

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateNullOrBlankDescription(requestDto));
    }

    @Test
    void testNullDescriptionShouldThrowException() {
        requestDto.setDescription(null);

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateNullOrBlankDescription(requestDto));
    }

    @Test
    void testCorrectDescriptionIsSuccessful() {
        requestDto.setDescription("Help me with java please!");

        assertDoesNotThrow(() -> requestValidator.validateNullOrBlankDescription(requestDto));
    }

    @Test
    void testValidateMentorshipRequestExistsShouldThrowException() {
        when(requestRepository.existsById(requestDto.getRequesterId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> requestValidator.validateMentorshipRequestExists(requestDto.getRequesterId()));
        verify(requestRepository, times(1)).existsById(requestDto.getRequesterId());
    }

    @Test
    void testValidateMentorshipRequestExistsSuccessful() {
        when(requestRepository.existsById(requestDto.getRequesterId())).thenReturn(true);

        assertDoesNotThrow(() -> requestValidator.validateMentorshipRequestExists(requestDto.getRequesterId()));
        verify(requestRepository, times(1)).existsById(requestDto.getRequesterId());
    }

    @Test
    void testNullRequestIdShouldThrowException() {
        requestDto.setId(null);

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateNullOrUnavailableId(requestDto.getId()));
    }

    @Test
    void testUnavailableRequestIdShouldThrowException() {
        requestDto.setId(0L);

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateNullOrUnavailableId(requestDto.getId()));
    }

    @Test
    void testValidateNullOrUnavailableIdSuccessful() {
        assertDoesNotThrow(() -> requestValidator.validateNullOrUnavailableId(requestDto.getId()));
    }

    @Test
    void testValidateRequesterHasReceiverAsMentorShouldThrowException() {
        when(requester.getMentors()).thenReturn(List.of(receiver));

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateRequesterHasReceiverAsMentor(requester, receiver));
        verify(requester, times(1)).getMentors();
    }

    @Test
    void testValidateRequesterHasReceiverAsMentorSuccessful() {
        when(requester.getMentors()).thenReturn(List.of());

        assertDoesNotThrow(() -> requestValidator.validateRequesterHasReceiverAsMentor(requester, receiver));
        verify(requester, times(1)).getMentors();
    }

    @Test
    void testNullRejectReasonShouldThrowException() {
        rejectionDto.setReason(null);

        assertThrows(InvalidMentorshipRejectException.class,
                () -> requestValidator.validateNullOrBlankRejectReason(rejectionDto));
    }

    @Test
    void testBlankRejectReasonShouldThrowException() {
        rejectionDto.setReason(BLANK_STRING);

        assertThrows(InvalidMentorshipRejectException.class,
                () -> requestValidator.validateNullOrBlankRejectReason(rejectionDto));
    }

    @Test
    void testValidateNullOrBlankRejectReasonSuccessful() {
        assertDoesNotThrow(() -> requestValidator.validateNullOrBlankRejectReason(rejectionDto));
    }
}