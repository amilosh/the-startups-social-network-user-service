package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import school.faang.user_service.service.mentorship.request_filter.RequestFilter;
import school.faang.user_service.validation.MentorshipRequestValidation;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private RequestFilter requestFilter;

    @Spy
    private MentorshipRequestMapperImpl mapper;

    @Mock
    private MentorshipRequestValidation validator;

    @Captor
    private ArgumentCaptor<MentorshipRequest> requestCaptor;

    private final long CORRECT_ID_1 = 1L;
    private final long CORRECT_ID_2 = 2L;
    private final long NON_EXIST_USER_ID = 123456L;
    private User receiver;
    private User requester;
    private MentorshipRequestDto mentorshipRequestDto;
    private MentorshipRequest mentorshipRequest;
    private LocalDateTime specificDate;

    @BeforeEach
    void initData() {
        receiver = User.builder()
                .id(CORRECT_ID_1)
                .username("Max")
                .email("max@mail.ru")
                .city("Amsterdam")
                .receivedMentorshipRequests(new ArrayList<>())
                .sentMentorshipRequests(new ArrayList<>())
                .build();
        requester = User.builder()
                .id(CORRECT_ID_2)
                .username("Denis")
                .email("denis@mail.ru")
                .city("New York")
                .receivedMentorshipRequests(new ArrayList<>())
                .sentMentorshipRequests(new ArrayList<>())
                .build();
        mentorshipRequestDto = MentorshipRequestDto.builder()
                .id(1L)
                .description("Запрос на менторство")
                .requesterId(CORRECT_ID_2)
                .receiverId(CORRECT_ID_1)
                .createdAt(LocalDateTime.of(2022, Month.APRIL, 2, 15, 20, 13))
                .build();


    }

    @Test
    public void testRequestMentorshipSavedSuccessfully() {

        doNothing().when(validator).validateSameId(CORRECT_ID_1, CORRECT_ID_2);

        when(validator.validateId(CORRECT_ID_1)).thenReturn(receiver);
        when(validator.validateId(CORRECT_ID_2)).thenReturn(requester);

        doNothing().when(validator).validate3MonthsFromTheLastRequest(requester);
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        List<MentorshipRequest> realList = requester.getSentMentorshipRequests();
        verify(mentorshipRequestRepository).save(requestCaptor.capture());
        verify(userRepository).save(requester);
        MentorshipRequest mentorshipRequest = requestCaptor.getValue();
        List<MentorshipRequest> expectedList = new ArrayList<>(Collections.singletonList(mentorshipRequest));

        assertEquals(expectedList, realList);
        assertEquals(mentorshipRequest.getRequester(), requester);
        assertEquals(mentorshipRequest.getReceiver(), receiver);
    }


}
