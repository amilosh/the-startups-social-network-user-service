package school.faang.user_service.validator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.InvalidMentorshipRequestException;
import school.faang.user_service.exception.UserNotExistsException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class MentorshipRequestValidatorTest {

    @Mock
    private MentorshipRequestRepository requestRepository;
    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private MentorshipRequestValidator requestValidator;

    private MentorshipRequestDto dto;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
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
        doThrow(new UserNotExistsException("User does not exists"))
                .when(userValidator).isUserExists(dto.getRequesterId());
        doNothing().when(userValidator).isUserExists(dto.getReceiverId());

        assertThrows(UserNotExistsException.class, () -> requestValidator.validateMentorshipRequest(dto));
    }

    @Test
    void testValidateReceiverDoesNotExist() {
        doNothing().when(userValidator).isUserExists(dto.getRequesterId());
        doThrow(new UserNotExistsException("User does not exist"))
                .when(userValidator).isUserExists(dto.getReceiverId());

        assertThrows(UserNotExistsException.class, () -> requestValidator.validateMentorshipRequest(dto));
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
}