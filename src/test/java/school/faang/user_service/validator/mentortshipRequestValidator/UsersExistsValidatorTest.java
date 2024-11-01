package school.faang.user_service.validator.mentortshipRequestValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.ValidationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsersExistsValidatorTest {
    @InjectMocks
    private UsersExistsValidator usersExistsValidator;

    @Mock
    private UserService userService;

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    ValidationContext validationContext;

    MentorshipRequestDto dto;

    @BeforeEach
    public void setUp() {
        validationContext = new ValidationContext(mentorshipRequestService, userService);
        dto = prepareData(1L, 2L, "description");
    }

    @Test
    public void testRequesterUsersExistsValidatorFailed() {
        testOnExistsUsers(dto.getRequesterUserId(), false);
        assertThrows(IllegalArgumentException.class,
                () -> usersExistsValidator.validate(dto, validationContext));
    }

    @Test
    public void testReceiverUsersExistsValidatorFailed() {
        testOnExistsUsers(dto.getRequesterUserId(), true);
        testOnExistsUsers(dto.getReceiverUserId(), false);

        assertThrows(IllegalArgumentException.class,
                () -> usersExistsValidator.validate(dto, validationContext));
    }

    @Test
    public void testUsersExistsValidatorSuccessful() {
        testOnExistsUsers(dto.getRequesterUserId(), true);
        testOnExistsUsers(dto.getReceiverUserId(), true);

        usersExistsValidator.validate(dto, validationContext);
    }

    private void testOnExistsUsers(long userId, boolean expected) {
        when(validationContext.userService().existsById(userId)).thenReturn(expected);
    }

    private MentorshipRequestDto prepareData(long requesterUserId, long receiverUserId, String description) {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterUserId(requesterUserId);
        dto.setReceiverUserId(receiverUserId);
        dto.setDescription(description);

        return dto;
    }
}
