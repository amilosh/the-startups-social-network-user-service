package school.faang.user_service.validator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.TestDataCreator;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
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

class MentorshipRequestValidatorTest {

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

    private AutoCloseable mocks;
    private MentorshipRequestDto dto;


    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        dto = TestDataCreator.createMentorshipRequestDto(1L, 1L, 2L, RequestStatus.PENDING, "help me with java.");
    }

    @AfterEach
    void closeMocks() {
        try {
            mocks.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testValidateRequesterDoesNotExist() {
        doThrow(new EntityNotFoundException("User does not exists"))
                .when(userValidator).isUserExists(dto.getRequesterId());
        doNothing().when(userValidator).isUserExists(dto.getReceiverId());

        assertThrows(EntityNotFoundException.class, () -> requestValidator.validateMentorshipRequest(dto));
    }

    @Test
    void testValidateReceiverDoesNotExist() {
        doNothing().when(userValidator).isUserExists(dto.getRequesterId());
        doThrow(new EntityNotFoundException("User does not exist"))
                .when(userValidator).isUserExists(dto.getReceiverId());

        assertThrows(EntityNotFoundException.class, () -> requestValidator.validateMentorshipRequest(dto));
    }

    @Test
    void testValidateOneRequestPerThreeMonthsShouldThrowException() {
        MentorshipRequest request = new MentorshipRequest();
        request.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")).minusMonths(1));

        when(requestRepository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId()))
                .thenReturn(Optional.of(request));

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateMentorshipRequest(dto));
    }

    @Test
    void testSelfRequestShouldThrowException() {
        dto.setReceiverId(dto.getRequesterId());
        MentorshipRequest request = new MentorshipRequest();
        request.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")).minusMonths(4));

        when(requestRepository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId()))
                .thenReturn(Optional.of(request));

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateMentorshipRequest(dto));
    }

    @Test
    void testValidateMentorshipRequestIsSuccessful() {
        MentorshipRequest request = new MentorshipRequest();
        request.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")).minusMonths(4));

        when(requestRepository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId()))
                .thenReturn(Optional.of(request));
        doNothing().when(userValidator).isUserExists(dto.getRequesterId());
        doNothing().when(userValidator).isUserExists(dto.getReceiverId());

        assertDoesNotThrow(() -> requestValidator.validateMentorshipRequest(dto));
    }

    @Test
    void testBlankDescriptionShouldThrowException() {
        dto.setDescription("   ");

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateNullOrBlankDescription(dto));
    }

    @Test
    void testNullDescriptionShouldThrowException() {
        dto.setDescription(null);

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateNullOrBlankDescription(dto));
    }

    @Test
    void testCorrectDescriptionIsSuccessful() {
        dto.setDescription("Help me with java please!");

        assertDoesNotThrow(() -> requestValidator.validateNullOrBlankDescription(dto));
    }

    @Test
    void testValidateMentorshipRequestExistsShouldThrowException() {
        when(requestRepository.existsById(dto.getRequesterId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> requestValidator.validateMentorshipRequestExists(dto.getRequesterId()));
        verify(requestRepository, times(1)).existsById(dto.getRequesterId());
    }

    @Test
    void testValidateMentorshipRequestExistsSuccessful() {
        when(requestRepository.existsById(dto.getRequesterId())).thenReturn(true);

        assertDoesNotThrow(() -> requestValidator.validateMentorshipRequestExists(dto.getRequesterId()));
        verify(requestRepository, times(1)).existsById(dto.getRequesterId());
    }

    @Test
    void testNullRequestIdShouldThrowException() {
        dto.setId(null);

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateNullOrUnavailableId(dto.getId()));
    }

    @Test
    void testUnavailableRequestIdShouldThrowException() {
        dto.setId(0L);

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateNullOrUnavailableId(dto.getId()));
    }

    @Test
    void testValidateNullOrUnavailableIdSuccessful() {
        assertDoesNotThrow(() -> requestValidator.validateNullOrUnavailableId(dto.getId()));
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
}