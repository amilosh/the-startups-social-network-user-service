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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void testCheckRequesterDoesNotExist() {
        when(userValidator.isUserExists(dto.getRequesterId()))
                .thenThrow(new UserNotExistsException("User does not exists"));
        when(userValidator.isUserExists(dto.getReceiverId())).thenReturn(true);

        assertThrows(UserNotExistsException.class, () -> requestValidator.validateMentorshipRequest(dto));
    }

    @Test
    void testCheckReceiverDoesNotExist() {
        when(userValidator.isUserExists(dto.getRequesterId())).thenReturn(true);
        when(userValidator.isUserExists(dto.getReceiverId()))
                .thenThrow(new UserNotExistsException("User does not exist"));

        assertThrows(UserNotExistsException.class, () -> requestValidator.validateMentorshipRequest(dto));
    }

    @Test
    void testCheckOneRequestPerThreeMonthsShouldThrowException() {
        MentorshipRequest request = new MentorshipRequest();
        request.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")).minusMonths(1));

        when(requestRepository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId()))
                .thenReturn(Optional.of(request));

        assertThrows(InvalidMentorshipRequestException.class,
                () -> requestValidator.validateMentorshipRequest(dto));
    }

    @Test
    void testSelfRequestShouldThrownException() {
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
        when(userValidator.isUserExists(dto.getRequesterId())).thenReturn(true);
        when(userValidator.isUserExists(dto.getReceiverId())).thenReturn(true);

        assertTrue(requestValidator.validateMentorshipRequest(dto));
    }
}