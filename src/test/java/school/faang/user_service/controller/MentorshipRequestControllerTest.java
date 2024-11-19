package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestCreateDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestControllerTest {

    @Mock
    private MentorshipRequestService requestService;

    @InjectMocks
    MentorshipRequestController mentorshipRequestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private MentorshipRequestCreateDto requestCreateDto;
    private MentorshipRequestDto expectedDto;
    private MentorshipRequestFilterDto filterDto;
    private RejectionDto rejectionDto;
    private long id;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorshipRequestController).build();
        objectMapper = new ObjectMapper();

        requestCreateDto = MentorshipRequestCreateDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("test description")
                .build();

        expectedDto = MentorshipRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .receiverId(2L)
                .description("test description")
                .status(RequestStatus.PENDING)
                .build();

        filterDto = MentorshipRequestFilterDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .descriptionPattern("test description")
                .status(RequestStatus.PENDING)
                .build();
        rejectionDto = RejectionDto.builder().reason("reason").build();
        id = expectedDto.getId();
    }

    @Test
    void testRequestMentorship() throws Exception {
        when(requestService.requestMentorship(requestCreateDto)).thenReturn(expectedDto);

        mockMvc.perform(post("/mentorship-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.description").value(expectedDto.getDescription()))
                .andExpect(jsonPath("$.requesterId").value(expectedDto.getRequesterId()))
                .andExpect(jsonPath("$.receiverId").value(expectedDto.getReceiverId()))
                .andExpect(jsonPath("$.status").value(expectedDto.getStatus().name()));

        verify(requestService, times(1)).requestMentorship(requestCreateDto);
    }

    @Test
    void testGetRequests() throws Exception {
        when(requestService.getRequests(filterDto)).thenReturn(List.of(expectedDto));
        String url = "/mentorship-requests/filtered"
                + (expectedDto.getDescription() != null ? "?description=" + expectedDto.getDescription() : "")
                + (expectedDto.getRequesterId() != null ? "&requesterId=" + expectedDto.getRequesterId() : "")
                + (expectedDto.getReceiverId() != null ? "&receiverId=" + expectedDto.getReceiverId() : "")
                + (expectedDto.getStatus() != null ? "&status=" + expectedDto.getStatus().name() : "");

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedDto.getId()))
                .andExpect(jsonPath("$[0].description").value(expectedDto.getDescription()))
                .andExpect(jsonPath("$[0].requesterId").value(expectedDto.getRequesterId()))
                .andExpect(jsonPath("$[0].receiverId").value(expectedDto.getReceiverId()))
                .andExpect(jsonPath("$[0].status").value(expectedDto.getStatus().name()));

        verify(requestService, times(1)).getRequests(filterDto);
    }

    @Test
    void testAcceptRequest() throws Exception {
        when(requestService.acceptRequest(id)).thenReturn(expectedDto);

        mockMvc.perform((put("/mentorship-requests/accepts/{id}", id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.description").value(expectedDto.getDescription()))
                .andExpect(jsonPath("$.requesterId").value(expectedDto.getRequesterId()))
                .andExpect(jsonPath("$.receiverId").value(expectedDto.getReceiverId()))
                .andExpect(jsonPath("$.status").value(expectedDto.getStatus().name()));

        verify(requestService, times(1)).acceptRequest(id);
    }

    @Test
    void testRejectRequest() throws Exception {
        when(requestService.rejectRequest(id, rejectionDto)).thenReturn(expectedDto);

        mockMvc.perform(patch("/mentorship-requests/rejects/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.description").value(expectedDto.getDescription()))
                .andExpect(jsonPath("$.requesterId").value(expectedDto.getRequesterId()))
                .andExpect(jsonPath("$.receiverId").value(expectedDto.getReceiverId()))
                .andExpect(jsonPath("$.status").value(expectedDto.getStatus().name()));

        verify(requestService, times(1)).rejectRequest(id, rejectionDto);
    }
}