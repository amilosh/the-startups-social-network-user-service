package school.faang.user_service.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.MentorshipRequestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MentorshipRequestController.class)
@Import({MentorshipRequestController.class})
public class MentorshipRequestControllerTest {
    @MockBean
    private MentorshipRequestService mentorshipRequestService;

    @MockBean
    private UserContext userContext;

    private Faker faker;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        faker = new Faker();
        MockitoAnnotations.openMocks(this);
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    private MentorshipRequestDto generateMentorshipRequestDto() {
        return new MentorshipRequestDto(
                faker.number().randomNumber(),
                faker.lorem().sentence(20),
                faker.number().randomNumber(),
                faker.number().randomNumber(),
                faker.options().option(RequestStatus.class),
                faker.bool().bool() ? faker.lorem().sentence(10) : null,
                LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)),
                LocalDateTime.now()
        );
    }

    private RequestFilterDto generateRequestFilterDto() {
        return new RequestFilterDto(
                faker.lorem().sentence(20),
                faker.number().randomNumber(),
                faker.number().randomNumber(),
                faker.options().option(RequestStatus.class)
        );
    }

    @Test
    public void testRequestMentorship_Success() throws Exception {
        MentorshipRequestDto requestDto = generateMentorshipRequestDto();

        mockMvc.perform(post("/api/user-service/request/{id}/create-request", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(mentorshipRequestService, times(1)).requestMentorship(requestDto);
    }

    @Test
    public void testRequestMentorship_BadRequest() throws Exception {
        MentorshipRequestDto requestDto = generateMentorshipRequestDto();

        mockMvc.perform(post("/api/user-service/request/{id}/create-request", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
        verify(mentorshipRequestService, never()).requestMentorship(requestDto);
    }

    @Test
    public void testGetRequests() throws Exception {
        RequestFilterDto filter = generateRequestFilterDto();
        List<MentorshipRequestDto> responseList = List.of(generateMentorshipRequestDto());
        when(mentorshipRequestService.getRequest(filter)).thenReturn(responseList);

        mockMvc.perform(get("/api/user-service/request/requests-filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseList)));

        verify(mentorshipRequestService, times(1)).getRequest(filter);
    }

    @Test
    public void testAcceptRequest() throws Exception {
        long requestId = 1L;

        mockMvc.perform(put("/api/user-service/request/{id}/accept", requestId))
                .andExpect(status().isOk());

        verify(mentorshipRequestService, times(1)).acceptRequest(requestId);
    }

    @Test
    public void testRejectRequest() throws Exception {
        Long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto("Reason for rejection");
        doNothing().when(mentorshipRequestService).rejectRequest(anyLong(), anyString());
        String request = objectMapper.writeValueAsString(rejectionDto);
        mockMvc.perform(put("/api/user-service/request/{id}/reject", requestId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
        verify(mentorshipRequestService).rejectRequest(eq(requestId), eq(rejectionDto.reason()));
    }
}
