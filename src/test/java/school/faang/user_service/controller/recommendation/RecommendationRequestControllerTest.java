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
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

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
    private static final String REJECTION_REASON = "Reason for rejection";

    private MockMvc mockMvc;

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    private RecommendationRequestDto requestDto;
    private RecommendationRequestDto responseDto;
    private  List<RecommendationRequestDto> responseList;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recommendationRequestController).build();

        requestDto = RecommendationRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .build();

        responseDto = RecommendationRequestDto.builder()
                .id(REQUEST_ID)
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .build();

        responseList = Collections.singletonList(responseDto);
    }

    @Test
    public void testRequestRecommendation() throws Exception {
        when(recommendationRequestService.create(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/recommendation-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requesterId\": " + REQUESTER_ID + ", \"receiverId\": " + RECEIVER_ID + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.requesterId").value(REQUESTER_ID))
                .andExpect(jsonPath("$.receiverId").value(RECEIVER_ID));

        verify(recommendationRequestValidator).validateRecommendationRequestDto(requestDto);
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
}
