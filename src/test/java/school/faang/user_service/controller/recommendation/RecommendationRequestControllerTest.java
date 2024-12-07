package school.faang.user_service.controller.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(RecommendationRequestController.class)
public class RecommendationRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationRequestService recommendationRequestService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;


    private RecommendationRequestDto requestDto;
    private RecommendationRequestDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = RecommendationRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .message("привет")
                .status(RequestStatus.PENDING)
                .skillIdentifiers(Arrays.asList(1L, 2L))
                .build();

        responseDto = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .receiverId(2L)
                .message("привет")
                .status(RequestStatus.PENDING)
                .skillIdentifiers(Arrays.asList(1L, 2L))
                .build();
    }


    @Test
    void testRequestRecommendation_Success() throws Exception {
        when(recommendationRequestService.create(any(RecommendationRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/recommendation-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.requesterId").value(responseDto.getRequesterId()))
                .andExpect(jsonPath("$.receiverId").value(responseDto.getReceiverId()))
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));

    }

    @Test
    void testRequestRecommendation_InvalidData() throws Exception {
        RecommendationRequestDto invalidRequestDto = RecommendationRequestDto.builder()
                .requesterId(null)
                .receiverId(2L)
                .message("hi")
                .status(RequestStatus.PENDING)
                .skillIdentifiers(Arrays.asList())
                .build();

        mockMvc.perform(post("/recommendation-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRequestRecommendation_ServiceException() throws Exception {
        doThrow(new RuntimeException("Service error"))
                .when(recommendationRequestService)
                .create(any(RecommendationRequestDto.class));

        mockMvc.perform(post("/recommendation-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Service error")));
    }

    @Test
    void testRequestRecommendation_MissingRequiredFields() throws Exception {
        RecommendationRequestDto invalidRequestDto = RecommendationRequestDto.builder()
                .receiverId(2L)
                .status(RequestStatus.PENDING)
                .skillIdentifiers(Arrays.asList(1L, 2L))
                .build();

        mockMvc.perform(post("/recommendation-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRecommendationById_Success() throws Exception {
        when(recommendationRequestService.getRequest(1L))
                .thenReturn(responseDto);

        mockMvc.perform(get("/recommendation-request/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

}
