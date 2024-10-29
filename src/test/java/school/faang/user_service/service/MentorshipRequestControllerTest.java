package school.faang.user_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.controller.MentorshipRequestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MentorshipRequestControllerTest {

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequestMentorship() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        doNothing().when(mentorshipRequestService).requestMentorship(mentorshipRequestDto);

        ResponseEntity<Void> response = mentorshipRequestController.requestMentorship(mentorshipRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mentorshipRequestService, times(1)).requestMentorship(mentorshipRequestDto);
    }

    @Test
    void testAcceptMentorship() {
        Long id = 1L;
        when(mentorshipRequestService.acceptMentorship(id)).thenReturn(id);

        ResponseEntity<Void> response = mentorshipRequestController.acceptMentorship(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mentorshipRequestService, times(1)).acceptMentorship(id);
    }

    @Test
    void testRejectRequest() {
        long id = 1L;
        RejectionDto rejectionDto = new RejectionDto();
        doNothing().when(mentorshipRequestService).rejectRequest(id, rejectionDto);

        ResponseEntity<Void> response = mentorshipRequestController.rejectRequest(id, rejectionDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mentorshipRequestService, times(1)).rejectRequest(id, rejectionDto);
    }

    @Test
    void testGetRequests() {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        List<MentorshipRequest> expectedRequests = Collections.singletonList(new MentorshipRequest());
        when(mentorshipRequestService.getRequests(requestFilterDto)).thenReturn(expectedRequests);

        ResponseEntity<List<MentorshipRequest>> response = mentorshipRequestController.getRequests(requestFilterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRequests, response.getBody());
        verify(mentorshipRequestService, times(1)).getRequests(requestFilterDto);
    }
}