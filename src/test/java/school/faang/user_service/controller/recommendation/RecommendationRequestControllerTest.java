package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class RecommendationRequestControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }

    @Test
    void testRequestRecommendationValidData() throws Exception {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto()
                .setMessage("Test")
                .setSkillIds(Arrays.asList(1L, 2L))
                .setRequesterId(Long.MAX_VALUE)
                .setReceiverId(Long.MAX_VALUE);

        when(recommendationRequestService.create(recommendationRequestDto)).thenReturn(recommendationRequestDto);


        String content = objectMapper.writeValueAsString(recommendationRequestDto);
        mockMvc.perform(post("/recommendation/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(content));
        verify(recommendationRequestService).create(recommendationRequestDto);
    }

    @Test
    void testRequestRecommendationInvalidData() throws Exception {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        MockHttpServletRequestBuilder post = post("/recommendation/request");

        testAutoValidateFieldsRequest(post, recommendationRequestDto);
        verify(recommendationRequestService, times(0)).create(any(RecommendationRequestDto.class));
    }


    @Test
    void testGetRecommendationRequestsValidData() throws Exception {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        RecommendationRequestDto recommendationRequestDto1 = new RecommendationRequestDto()
                .setRequesterId(Long.MAX_VALUE);
        RecommendationRequestDto recommendationRequestDto2 = new RecommendationRequestDto()
                .setRequesterId(Long.MAX_VALUE);
        List<RecommendationRequestDto> list = Arrays.asList(recommendationRequestDto1, recommendationRequestDto2);
        String expectedJson = objectMapper.writeValueAsString(list);

        when(recommendationRequestService.getRequests(requestFilterDto)).thenReturn(list);

        mockMvc.perform(post("/recommendation/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFilterDto)))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
        verify(recommendationRequestService).getRequests(requestFilterDto);
    }

    @Test
    void testGetRecommendationRequestCorrectRequest() throws Exception {
        long id = 1;
        RecommendationRequestDto returned = new RecommendationRequestDto()
                .setId(id);
        String expected = objectMapper.writeValueAsString(returned);

        when(recommendationRequestService.getRequest(id)).thenReturn(returned);


        mockMvc.perform(get("/recommendation/request/" + id))
                .andExpect(MockMvcResultMatchers.content().json(expected));
        verify(recommendationRequestService).getRequest(id);
    }

    @Test
    void testRejectRequestValidData() throws Exception {
        RejectionDto rejectionDto = new RejectionDto()
                .setReason("Test");
        long id = 1;
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto()
                .setStatus(RequestStatus.REJECTED);
        String expected = objectMapper.writeValueAsString(recommendationRequestDto);
        String sent = objectMapper.writeValueAsString(rejectionDto);
        when(recommendationRequestService.rejectRequest(id, rejectionDto)).thenReturn(recommendationRequestDto);

        mockMvc.perform(put("/recommendation/request/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sent))
                .andExpect(MockMvcResultMatchers.content().json(expected));
        verify(recommendationRequestService).rejectRequest(id, rejectionDto);
    }

    @Test
    void testRejectRequestInvalidData() throws Exception {
        RejectionDto rejectionDto = new RejectionDto();
        int id = 1;
        MockHttpServletRequestBuilder put = put("/recommendation/request/" + id);

        testAutoValidateFieldsRequest(put, rejectionDto);
        verify(recommendationRequestService, times(0)).rejectRequest(id, rejectionDto);
    }

    private void testAutoValidateFieldsRequest(MockHttpServletRequestBuilder request, Object invalidObject) throws Exception {
        mockMvc.perform(request
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidObject)))
                .andExpect(status().isBadRequest());
    }
}