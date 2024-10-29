package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Test
    public void testCreateMentorshipRequestValidationFailed() {
        MentorshipRequestDto dto = new MentorshipRequestDto();

        doThrow(new IllegalArgumentException()).when(mentorshipRequestValidator).validateMentorshipRequest(dto);

        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.createRequestMentorship(dto));

    }

    @Test
    public void testCreateMentorshipRequestValidationSuccessful() {
        MentorshipRequestDto dto = prepareData(1L, 2L, "description");

        mentorshipRequestService.createRequestMentorship(dto);

        verify(mentorshipRequestValidator).validateMentorshipRequest(dto);
        verify(mentorshipRequestRepository).create(
                dto.getRequesterUserId(),
                dto.getReceiverUserId(),
                dto.getDescription()
        );
    }


    @Test
    public void testFindLatestRequest() {
        assertFindLatestRequest(1L, 2L, Optional.of(new MentorshipRequest()));
    }

    @Test
    public void testNotFoundLastRequest() {
        assertFindLatestRequest(1L, 2L, Optional.empty());
    }

    private void assertFindLatestRequest(long requesterId, long receiverId, Optional<MentorshipRequest> expectedResult) {
        MentorshipRequestDto dto = prepareData(requesterId, receiverId, "description");

        when(mentorshipRequestRepository.findLatestRequest(dto.getRequesterUserId(), dto.getReceiverUserId())).thenReturn(expectedResult);
        Optional<MentorshipRequest> result = mentorshipRequestService.findLatestRequest(dto.getRequesterUserId(), dto.getReceiverUserId());
        assertEquals(expectedResult.isPresent(), result.isPresent());
    }

    private MentorshipRequestDto prepareData(long requesterId, long receiverId, String description) {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterUserId(requesterId);
        dto.setReceiverUserId(receiverId);
        dto.setDescription(description);

        return dto;
    }

}
