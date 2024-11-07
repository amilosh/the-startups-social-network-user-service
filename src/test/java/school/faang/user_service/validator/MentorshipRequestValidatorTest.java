package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {

    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;

    private MentorshipRequestDto dto;
    private boolean requesterUserExists;
    private boolean receiverUserExists;
    private LocalDateTime createAt;

    @BeforeEach
    public void setUp() {
        dto = new MentorshipRequestDto();
        dto.setRequesterUserId(1L);
        dto.setReceiverUserId(2L);

        requesterUserExists = true;
        receiverUserExists = true;

        createAt = LocalDateTime.now().minusMonths(4);
    }

    @Test
    public void testSameUserValidator() {
        dto.setReceiverUserId(1L);
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validate(dto, requesterUserExists, receiverUserExists, createAt));
    }

    @Test
    public void testRequesterUserExists() {
        requesterUserExists = false;
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validate(dto, requesterUserExists, receiverUserExists, createAt));
    }

    @Test
    public void testReceiverUserExists() {
        receiverUserExists = false;
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validate(dto, requesterUserExists, receiverUserExists, createAt));
    }

    @Test
    public void testCreateAtRequestIsAfterThreeMonths() {
        createAt = LocalDateTime.now().minusMonths(2);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validate(dto, requesterUserExists, receiverUserExists, createAt));
    }

    @Test
    public void testValidateSuccessful() {
        mentorshipRequestValidator.validate(dto, requesterUserExists, receiverUserExists, createAt);
    }
}
