package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {

    @InjectMocks
    private MentorshipRequestValidator validator;

    @Mock
    private UserService userService;

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @Test
    public void testShouldThrowExceptionWhenDescriptionIsBlank() {
        MentorshipRequestDto dto = prepareData("  ", 1L, 2L);

        assertThrows(IllegalArgumentException.class, () ->
                validator.validateMentorshipRequest(dto));
    }

    @Test
    public void testShouldThrowExceptionWhenRequesterUserNotFound() {
        MentorshipRequestDto dto = prepareData("description", 1L, 2L);

        when(userService.existsById(dto.getRequesterUserId())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                validator.validateMentorshipRequest(dto)
        );
    }

    @Test
    public void testShouldThrowExceptionWhenReceiverUserNotFound() {
        MentorshipRequestDto dto = prepareData("description", 1L, 2L);

        when(userService.existsById(dto.getRequesterUserId())).thenReturn(true);
        when(userService.existsById(dto.getReceiverUserId())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                validator.validateMentorshipRequest(dto)
        );
    }

    @Test
    public void testShouldThrowExceptionWhenRequesterIsSameAsReceiver() {
        MentorshipRequestDto dto = prepareData("description", 1L, 1L);

        when(userService.existsById(dto.getRequesterUserId())).thenReturn(true);
        when(userService.existsById(dto.getReceiverUserId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                validator.validateMentorshipRequest(dto)
        );
    }

    @Test
    public void testShouldThrowExceptionWhenLatestRequestIsNotOlderThanThreeMonths() {
        MentorshipRequestDto dto = prepareData("description", 1L, 2L);
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setCreatedAt(LocalDateTime.now());

        when(userService.existsById(dto.getRequesterUserId())).thenReturn(true);
        when(userService.existsById(dto.getReceiverUserId())).thenReturn(true);
        when(mentorshipRequestService.findLatestRequest(dto.getRequesterUserId(), dto.getReceiverUserId()))
                .thenReturn(Optional.of(mentorshipRequest));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validateMentorshipRequest(dto)
        );
    }

    @Test
    public void testShouldNotThrowExceptionForValidRequest() {
        MentorshipRequestDto dto = prepareData("description", 1L, 2L);
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setCreatedAt(LocalDateTime.now().minusMonths(4));

        when(userService.existsById(dto.getRequesterUserId())).thenReturn(true);
        when(userService.existsById(dto.getReceiverUserId())).thenReturn(true);
        when(mentorshipRequestService.findLatestRequest(dto.getRequesterUserId(), dto.getReceiverUserId()))
                .thenReturn(Optional.of(mentorshipRequest));

        assertDoesNotThrow(() -> validator.validateMentorshipRequest(dto));
    }


    private MentorshipRequestDto prepareData(String description, long requesterUserId, long receiverUserId) {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setDescription(description);
        dto.setRequesterUserId(requesterUserId);
        dto.setReceiverUserId(receiverUserId);
        return dto;
    }
}
