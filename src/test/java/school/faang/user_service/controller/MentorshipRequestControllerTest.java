package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestControllerTest {

    @Mock
    private MentorshipRequestService requestService;
    @Mock
    private MentorshipRequestValidator requestValidator;
    @Spy
    private MentorshipRequestMapper requestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    @InjectMocks
    private MentorshipRequestController requestController;

    private MentorshipRequest firstRequest;
    private MentorshipRequest secondRequest;
    private RequestFilterDto filterDto;
    private MentorshipRequestDto firstRequestDto;
    private MentorshipRequestDto secondRequestDto;
    private RejectionDto rejectionDto;

    @BeforeEach
    void setUp() {
        firstRequest = MentorshipRequest.builder().id(1L).build();
        secondRequest = MentorshipRequest.builder().id(2L).build();
        filterDto = RequestFilterDto.builder().build();
        rejectionDto = RejectionDto.builder().build();
        firstRequestDto = requestMapper.toDto(firstRequest);
        secondRequestDto = requestMapper.toDto(secondRequest);
    }

    @Test
    void testControllerCreateRequest() {
        requestController.requestMentorship(firstRequestDto);

        verify(requestService, times(1)).requestMentorship(any(MentorshipRequestDto.class));
    }

    @Test
    void testControllerGettingRequests() {
        List<MentorshipRequestDto> dtos = List.of(firstRequestDto, secondRequestDto);
        when(requestService.getRequests(filterDto)).thenReturn(dtos);

        List<MentorshipRequestDto> result = requestController.getRequests(filterDto).getBody();

        verify(requestService, times(1)).getRequests(filterDto);
        assertEquals(dtos, result);
    }

    @Test
    void testControllerAcceptRequest() {
        long id = firstRequest.getId();
        when(requestService.acceptRequest(id)).thenReturn(firstRequestDto);

        MentorshipRequestDto result = requestController.acceptRequest(id).getBody();

        verify(requestService, times(1)).acceptRequest(id);
        assertEquals(result, firstRequestDto);
    }

    @Test
    void testControllerRejectRequest() {
        long id = firstRequest.getId();
        when(requestService.rejectRequest(id, rejectionDto)).thenReturn(firstRequestDto);

        MentorshipRequestDto result = requestController.rejectRequest(id, rejectionDto).getBody();

        verify(requestService, times(1)).rejectRequest(id, rejectionDto);
        assertEquals(result, firstRequestDto);
    }
}