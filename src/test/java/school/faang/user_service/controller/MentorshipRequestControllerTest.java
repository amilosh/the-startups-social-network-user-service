package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestStatusDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.utilities.UrlUtils;

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

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    public void createRequestMentorshipSuccessTest() throws Exception {
        MentorshipRequestDto requestDto = new MentorshipRequestDto("sdfsddsfadsfdafefdferw213213qdfdsfsdfadfadsf", 3L, 4L);

        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.MENTORSHIP + UrlUtils.REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(mentorshipRequestService).requestMentorship(requestDto);
    }

    @Test
    public void createRequestMentorshipWithFieldDescriptionLess25SymbolsFailTest() throws Exception {
        MentorshipRequestDto requestDto = new MentorshipRequestDto("less25symbols", 1L, 1L);

        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.MENTORSHIP + UrlUtils.REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(mentorshipRequestService, never()).requestMentorship(requestDto);
    }

    @Test
    public void testGetRequestsSuccessTest() throws Exception {
        List<MentorshipRequestDto> responseList = List.of(new MentorshipRequestDto("test", 1L, 2L));

        when(mentorshipRequestService.getRequest("test", 1L, 2L, RequestStatusDto.PENDING)).thenReturn(responseList);
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.MENTORSHIP + UrlUtils.REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("description", "test")
                        .param("requesterId", "1")
                        .param("receiverId", "2")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseList)));

        verify(mentorshipRequestService).getRequest("test", 1L, 2L, RequestStatusDto.PENDING);
    }

    @Test
    public void testAcceptRequestSuccessTest() throws Exception {
        long requestId = 1L;

        mockMvc.perform(put(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.MENTORSHIP + UrlUtils.REQUEST + UrlUtils.ID + UrlUtils.ACCEPT, requestId))
                .andExpect(status().isOk());

        verify(mentorshipRequestService).acceptRequest(requestId);
    }

    @Test
    public void testRejectRequestSuccessTest() throws Exception {
        long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto("Reason for rejection");
        doNothing().when(mentorshipRequestService).rejectRequest(anyLong(), anyString());
        String request = objectMapper.writeValueAsString(rejectionDto);

        mockMvc.perform(put(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.MENTORSHIP + UrlUtils.REQUEST + UrlUtils.ID + UrlUtils.REJECT, requestId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());

        verify(mentorshipRequestService).rejectRequest(requestId, rejectionDto.reason());
    }
}