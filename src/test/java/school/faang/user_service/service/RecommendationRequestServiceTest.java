package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRecommendationRequest_Success() {

        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setMessage("Пожалуйста дай рекомендацию тест");
        dto.setSkills(Arrays.asList(1L, 2L, 3L));

        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.empty());
        when(skillRepository.countExisting(dto.getSkills())).thenReturn(dto.getSkills().size());

        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(2L);
        Skill skill3 = new Skill();
        skill2.setId(3L);

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill1));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(skill2));
        when(skillRepository.findById(3L)).thenReturn(Optional.of(skill3));

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setMessage(dto.getMessage());

        when(recommendationRequestMapper.toEntity(dto)).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenAnswer(invocation -> {
                    RecommendationRequest savedRequest = invocation.getArgument(0);
                    savedRequest.setId(1L);
                    return savedRequest;
                });
        when(recommendationRequestMapper.toDto(any(RecommendationRequest.class))).thenReturn(dto);

        RecommendationRequestDto result = recommendationRequestService.create(dto);

        assertNotNull(result);
        assertEquals(dto.getMessage(), result.getMessage());
        verify(recommendationRequestRepository, times(1))
                .save(any(RecommendationRequest.class));
        verify(skillRequestRepository, times(dto.getSkills().size())).save(any());
    }

    @Test
    void testCreateRecommendationRequest_RequesterNotFound() {
        RecommendationRequestDto dto = new RecommendationRequestDto();

        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setMessage("Пожалуйста дай рекомендацию");
        dto.setSkills(Arrays.asList(1L, 2L, 3L));

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(dto);
        });

        assertEquals("Пользователя, запрашивающего рекомендацию не существует", exception.getMessage());
    }

    @Test
    void testCreateRecommendationRequest_ReceiverNotFound() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setMessage("Пожалуйста дай рекомендацию");
        dto.setSkills(Arrays.asList(1L, 1L));

        User requester = new User();
        requester.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(dto);
        });

        assertEquals("Пользователя, получающего рекомендацию не существует", exception.getMessage());
    }

    @Test
    void testCreateRecommendationRequest_RequestLimitExceeded() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setMessage("Пожалуйста дай рекомендацию");
        dto.setSkills(Arrays.asList(1L, 1L));


        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        RecommendationRequest lastRequest = new RecommendationRequest();
        lastRequest.setCreatedAt(LocalDateTime.now().minusMonths(3));

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.of(lastRequest));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(dto);
        });

        assertEquals("Запрос этому пользователю можно отправлять только раз в полгода", exception.getMessage());
    }

    @Test
    void testCreateRecommendationRequest_SkillsNotFound() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setMessage("Пожалуйста дай рекомендацию");
        dto.setSkills(Arrays.asList(1L, 2L, 3L));

        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.empty());
        when(skillRepository.countExisting(dto.getSkills())).thenReturn(2);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(dto);
        });

        assertEquals("Некоторых скиллов нет в базе данных", exception.getMessage());
    }

    @Test
    void testGetRequests_WithAllFilters() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setStatus(RequestStatus.PENDING);
        filter.setRequesterId(1L);
        filter.setReceiverId(2L);
        filter.setStartDate(LocalDateTime.of(2024, 1, 1, 0, 0));
        filter.setEndDate(LocalDateTime.of(2024, 12, 31, 23, 59));

        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        RecommendationRequest requestOne = new RecommendationRequest();
        requestOne.setId(1L);
        requestOne.setStatus(RequestStatus.PENDING);
        requestOne.setCreatedAt(LocalDateTime.of(2024, 8, 21, 12, 0));
        requestOne.setRequester(requester);
        requestOne.setReceiver(receiver);

        RecommendationRequestDto dtoOne = new RecommendationRequestDto();
        dtoOne.setId(1L);
        dtoOne.setMessage("Пожалуйста дай мне рекомендацию");
        dtoOne.setRequesterId(1L);
        dtoOne.setReceiverId(2L);
        dtoOne.setStatus(RequestStatus.PENDING);
        dtoOne.setCreatedAt(LocalDateTime.of(2024, 8, 21, 12, 0));
        dtoOne.setSkills(Arrays.asList(1L, 2L));

        List<RecommendationRequest> requestsList = Arrays.asList(requestOne);

        when(recommendationRequestRepository.findAll()).thenReturn(requestsList);
        when(recommendationRequestMapper.toDto(requestOne)).thenReturn(dtoOne);

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dtoOne.getId(), result.get(0).getId());

        verify(recommendationRequestRepository, times(1)).findAll();
    }

    @Test
    void testGetRequests_NoFilters() {
        RequestFilterDto filter = new RequestFilterDto();

        RecommendationRequest requestOne = new RecommendationRequest();
        requestOne.setId(1L);
        requestOne.setStatus(RequestStatus.ACCEPTED);
        requestOne.setCreatedAt(LocalDateTime.now());

        RecommendationRequestDto dtoOne = new RecommendationRequestDto();
        dtoOne.setId(1L);
        dtoOne.setMessage("Пожалуйста дай мне рекомендацию");
        dtoOne.setRequesterId(1L);
        dtoOne.setReceiverId(2L);
        dtoOne.setStatus(RequestStatus.ACCEPTED);
        dtoOne.setCreatedAt(requestOne.getCreatedAt());
        dtoOne.setSkills(Arrays.asList(1L, 2L));

        RecommendationRequest requestTwo = new RecommendationRequest();
        requestTwo.setId(2L);
        requestTwo.setStatus(RequestStatus.REJECTED);
        requestTwo.setCreatedAt(LocalDateTime.now().minusDays(10));

        RecommendationRequestDto dtoTwo = new RecommendationRequestDto();
        dtoTwo.setId(2L);
        dtoTwo.setMessage("Пожалуйста дай мне рекомендацию");
        dtoTwo.setRequesterId(1L);
        dtoTwo.setReceiverId(2L);
        dtoTwo.setStatus(RequestStatus.REJECTED);
        dtoTwo.setCreatedAt(requestTwo.getCreatedAt());
        dtoTwo.setSkills(Arrays.asList(1L, 2L));

        when(recommendationRequestRepository.findAll()).thenReturn(Arrays.asList(requestOne, requestTwo));
        when(recommendationRequestMapper.toDto(requestOne)).thenReturn(dtoOne);
        when(recommendationRequestMapper.toDto(requestTwo)).thenReturn(dtoTwo);

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filter);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dtoOne.getId(), result.get(0).getId());
        assertEquals(dtoTwo.getId(), result.get(1).getId());

        verify(recommendationRequestRepository, times(1)).findAll();
    }

    @Test
    void testGetRequests_NoResults() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setStatus(RequestStatus.REJECTED);

        when(recommendationRequestRepository.findAll()).thenReturn(Arrays.asList());

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filter);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(recommendationRequestRepository, times(1)).findAll();
    }

    @Test
    void getRequest_Success() {
        Long id = 1L;
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        RecommendationRequest request = new RecommendationRequest();
        request.setId(id);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setRequester(requester);
        request.setReceiver(receiver);

        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(id);
        dto.setMessage("Пожалуйста дай мне рекомендацию");
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setStatus(RequestStatus.PENDING);
        dto.setCreatedAt(request.getCreatedAt());
        dto.setSkills(Arrays.asList(1L, 2L));

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(request));
        when(recommendationRequestMapper.toDto(request)).thenReturn(dto);

        RecommendationRequestDto result = recommendationRequestService.getRequest(id);

        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getRequesterId(), result.getRequesterId());
        assertEquals(dto.getReceiverId(), result.getReceiverId());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestMapper, times(1)).toDto(request);
    }

    @Test
    void getRequest_NotFound() {
        Long id = 99L;

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        GlobalExceptionHandler.RecommendationRequestNotFoundException exception =
                assertThrows(GlobalExceptionHandler.RecommendationRequestNotFoundException.class, () -> {
                    recommendationRequestService.getRequest(id);
                });

        assertEquals("Запрос на рекомендацию с таким id не найден", exception.getMessage());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestMapper, never()).toDto(any());
    }

    @Test
    void rejectRequest_Success() {
        Long id = 1L;
        RejectionDto rejection = new RejectionDto();
        rejection.setReason("Просто не хочу. Ты мне неприятен.");

        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        RecommendationRequest request = new RecommendationRequest();
        request.setId(id);
        request.setMessage("PПожалуйста, дай мне рекомендацию");
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setRejectionReason(null);

        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(id);
        dto.setMessage("Пожалуйста, дай мне рекомендацию");
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setStatus(RequestStatus.REJECTED);
        dto.setCreatedAt(request.getCreatedAt());
        dto.setRejectionReason("Просто не хочу. Ты мне неприятен.");

        when(recommendationRequestRepository.findById(id)).thenReturn(java.util.Optional.of(request));
        when(recommendationRequestRepository.save(request)).thenReturn(request);
        when(recommendationRequestMapper.toDto(request)).thenReturn(dto);

        RecommendationRequestDto result = recommendationRequestService.rejectRequest(id, rejection);

        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getStatus(), RequestStatus.REJECTED);
        assertEquals(dto.getRejectionReason(), "Просто не хочу. Ты мне неприятен.");

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, times(1)).save(request);
        verify(recommendationRequestMapper, times(1)).toDto(request);
    }

    @Test
    void rejectRequest_AlreadyRejected() {
        Long id = 1L;
        RejectionDto rejection = new RejectionDto();
        rejection.setReason("Duplicate request");

        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        RecommendationRequest request = new RecommendationRequest();
        request.setId(id);
        request.setMessage("Пожалуйста, дай мне рекомендацию");
        request.setStatus(RequestStatus.REJECTED);
        request.setCreatedAt(LocalDateTime.now());
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setRejectionReason("Просто не хочу. Ты мне неприятен.");

        when(recommendationRequestRepository.findById(id)).thenReturn(java.util.Optional.of(request));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            recommendationRequestService.rejectRequest(id, rejection);
        });

        assertEquals("Невозможно отклонить запрос на рекомендацию, поскольку он уже имеет статус REJECTED",
                exception.getMessage());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
    }

    @Test
    void rejectRequest_NotFound() {
        Long id = 99L;
        RejectionDto rejection = new RejectionDto();
        rejection.setReason("Invalid request");

        when(recommendationRequestRepository.findById(id)).thenReturn(java.util.Optional.empty());

        GlobalExceptionHandler.RecommendationRequestNotFoundException exception
                = assertThrows(GlobalExceptionHandler.RecommendationRequestNotFoundException.class, () -> {
            recommendationRequestService.rejectRequest(id, rejection);
        });

        assertEquals("Запрос на рекомендацию с таким id не найден", exception.getMessage());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
    }
}


