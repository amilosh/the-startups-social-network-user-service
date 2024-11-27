package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {
    private static final Long REQUESTER_ID = 1L;
    private static final Long RECEIVER_ID = 2L;
    private static final Long REQUEST_ID = 1L;
    private static final Long RESPONSE_ID = 1L;
    private static final String MESSAGE = "Message";
    private static final String REJECTION_REASON = "Reason for rejection";
    private static final String SKILL_TITLE = "Java";
    private static final Long SKILL_ID = 1L;

    private MockMvc mockMvc;

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    private RecommendationRequestDto requestDto;
    private RecommendationRequestDto responseDto;
    private List<RecommendationRequestDto> responseList;
    private ResponseRecommendationDto responseRecommendationDto;
    private Skill skill;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recommendationRequestController).build();

        skill = Skill.builder()
                .id(SKILL_ID)
                .title(SKILL_TITLE)
                .build();

        SkillRequestDto skillRequestDto = SkillRequestDto.builder()
                .skillId(skill.getId())
                .skillTitle(skill.getTitle())
                .build();

        List<SkillRequestDto> skillRequestDtoList = List.of(skillRequestDto);

        requestDto = RecommendationRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .message(MESSAGE)
                .skillRequests(skillRequestDtoList)
                .build();

        responseDto = RecommendationRequestDto.builder()
                .id(REQUEST_ID)
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .message(MESSAGE)
                .skillRequests(skillRequestDtoList)
                .build();

        responseList = Collections.singletonList(responseDto);

        responseRecommendationDto = ResponseRecommendationDto.builder()
                .id(RESPONSE_ID)
                .authorId(RECEIVER_ID)
                .receiverId(REQUEST_ID)
                .build();
    }

    @Test
    public void testRequestRecommendation() throws Exception {
        when(recommendationRequestService.create(requestDto)).thenReturn(responseDto);

        String validJsonRequest = """
                    {
                        "requesterId": %d,
                        "receiverId": %d,
                        "message": "%s",
                        "skillRequests": [
                            {
                                "skillId": %s,
                                "skillTitle": "%s"
                            }
                        ]
                    }
                """.formatted(REQUESTER_ID, RECEIVER_ID, MESSAGE, skill.getId(), SKILL_TITLE);

        mockMvc.perform(post("/api/v1/recommendation-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.requesterId").value(REQUESTER_ID))
                .andExpect(jsonPath("$.receiverId").value(RECEIVER_ID))
                .andExpect(jsonPath("$.message").value(MESSAGE))
                .andExpect(jsonPath("$.skillRequests[0].skillId").value(skill.getId()))
                .andExpect(jsonPath("$.skillRequests[0].skillTitle").value(SKILL_TITLE));


        verify(recommendationRequestService).create(requestDto);
    }

    @Test
    public void testGetRecommendationRequests() throws Exception {
        when(recommendationRequestService.getRequests(any())).thenReturn(responseList);

        mockMvc.perform(get("/api/v1/recommendation-requests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID))
                .andExpect(jsonPath("$[0].requesterId").value(REQUESTER_ID))
                .andExpect(jsonPath("$[0].receiverId").value(RECEIVER_ID));

        verify(recommendationRequestService).getRequests(any());
    }

    @Test
    public void testGetRecommendationRequest() throws Exception {
        when(recommendationRequestService.getRequest(REQUEST_ID)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/recommendation-requests/" + REQUEST_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.requesterId").value(REQUESTER_ID))
                .andExpect(jsonPath("$.receiverId").value(RECEIVER_ID));

        verify(recommendationRequestService).getRequest(REQUEST_ID);
    }

    @Test
    public void testRejectRequest() throws Exception {
        when(recommendationRequestService.rejectRequest(eq(REQUEST_ID), any())).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/recommendation-requests/" + REQUEST_ID + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\": \"" + REJECTION_REASON + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.requesterId").value(REQUESTER_ID))
                .andExpect(jsonPath("$.receiverId").value(RECEIVER_ID));

        verify(recommendationRequestService).rejectRequest(eq(REQUEST_ID), any());
    }

    @Test
    public void testAcceptRequest() throws Exception {
        when(recommendationRequestService.acceptRequest(REQUEST_ID)).thenReturn(responseRecommendationDto);

        mockMvc.perform(put("/api/v1/recommendation-requests/" + REQUEST_ID + "/accept")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(RESPONSE_ID))
                .andExpect(jsonPath("$.authorId").value(RECEIVER_ID))
                .andExpect(jsonPath("$.receiverId").value(REQUESTER_ID));

        verify(recommendationRequestService).acceptRequest(REQUEST_ID);
    }
}
