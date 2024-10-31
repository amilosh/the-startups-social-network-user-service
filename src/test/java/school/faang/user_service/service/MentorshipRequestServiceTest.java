package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    private MentorshipRequestMapper mentorshipRequestMapper;

    @Mock
    private List<MentorshipRequestFilter> mentorshipRequestFilters;

    @Mock
    private UserService userService;

    @Mock
    MentorshipRequestValidator mentorshipRequestValidator;

    List<MentorshipRequestValidator> mentorshipRequestValidators;

    @BeforeEach
    public void setUp() {
        mentorshipRequestValidators = new ArrayList<>();
        mentorshipRequestValidators.add(mentorshipRequestValidator);

        mentorshipRequestService = new MentorshipRequestService(
                mentorshipRequestRepository,
                mentorshipRequestMapper,
                mentorshipRequestFilters,
                mentorshipRequestValidators,
                userService
        );
    }

    @Test
    public void testCreateMentorshipRequestValidationFailed() {
        mentorshipRequestValidators.add(mentorshipRequestValidator);

        doThrow(new IllegalArgumentException()).when(mentorshipRequestValidator).validate(any(), any());

        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.createRequestMentorship(new MentorshipRequestDto()));

    }

    @Test
    public void testGetMentorshipRequest() {
        mentorshipRequestService.getRequests(new RequestFilterDto());
        verify(mentorshipRequestRepository).findAll();
    }


    @Test
    public void testCreateMentorshipRequestValidationSuccessful() {
        MentorshipRequestDto dto = prepareData(1L, 2L, "description");

        mentorshipRequestService.createRequestMentorship(dto);

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
