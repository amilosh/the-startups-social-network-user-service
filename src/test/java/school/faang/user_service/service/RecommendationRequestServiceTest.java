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
import school.faang.user_service.exception.RecommendationRequestNotFoundException;
import school.faang.user_service.filter.RecommendationRequestFilterManager;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;
import school.faang.user_service.validator.SkillValidator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @Mock
    private UserService userService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillRequestService skillRequestService;

    @Mock
    private SkillValidator skillValidator;

    @Mock
    private RecommendationRequestFilterManager filterManager;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    private RecommendationRequestValidator recommendationRequestValidator;

    private User requester;
    private User receiver;
    private Skill skill1;
    private Skill skill2;
    private Skill skill3;
    private RecommendationRequest recommendationRequest;
    private RecommendationRequestDto recommendationRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        RecommendationRequestValidator realValidator = new RecommendationRequestValidator(recommendationRequestRepository);
        recommendationRequestValidator = spy(realValidator);

        recommendationRequestService = new RecommendationRequestService(
                recommendationRequestRepository,
                recommendationRequestMapper,
                skillRequestService,
                skillValidator,
                userService,
                filterManager,
                recommendationRequestValidator
        );

        requester = User.builder()
                .id(1L)
                .build();

        receiver = User.builder()
                .id(2L)
                .build();

        skill1 = Skill.builder()
                .id(1L)
                .title("Java")
                .build();

        skill2 = Skill.builder()
                .id(2L)
                .title("Kotlin")
                .build();

        skill3 = Skill.builder()
                .id(3L)
                .title("Hibernate")
                .build();

        recommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .message("Пожалуйста, дай мне рекомендацию")
                .build();

        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .receiverId(2L)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .skills(Arrays.asList(1L, 2L, 3L))
                .message("Пожалуйста, дай мне рекомендацию")
                .build();
    }

    @Test
    void testCreateRecommendationRequest_Success() {
        doNothing().when(skillValidator).validateSkills(recommendationRequestDto.getSkills());

        when(userService.getUserById(1L)).thenReturn(requester);
        when(userService.getUserById(2L)).thenReturn(receiver);
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.empty());
        doNothing().when(skillValidator).validateSkills(recommendationRequestDto.getSkills());

        when(recommendationRequestMapper.toEntity(any(RecommendationRequestDto.class)))
                .thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenAnswer(invocation -> {
                    RecommendationRequest savedRequest = invocation.getArgument(0);
                    savedRequest.setId(1L);
                    return savedRequest;
                });
        when(recommendationRequestMapper.toDto(any(RecommendationRequest.class)))
                .thenReturn(recommendationRequestDto);

        when(skillRepository.findAllById(recommendationRequestDto.getSkills()))
                .thenReturn(Arrays.asList(skill1, skill2, skill3));

        RecommendationRequestDto result = recommendationRequestService.create(recommendationRequestDto);

        assertNotNull(result);
        assertEquals(recommendationRequestDto.getMessage(), result.getMessage());
        assertEquals(RequestStatus.PENDING, result.getStatus());
        assertEquals(1L, result.getId());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).getUserById(2L);
        verify(recommendationRequestRepository, times(1))
                .findLatestPendingRequest(1L, 2L);
        verify(skillValidator, times(1)).validateSkills(recommendationRequestDto.getSkills());
        verify(recommendationRequestMapper, times(1))
                .toEntity(recommendationRequestDto);
        verify(recommendationRequestRepository, times(1))
                .save(recommendationRequest);
        verify(recommendationRequestMapper, times(1))
                .toDto(recommendationRequest);
    }

    @Test
    void testCreateRecommendationRequest_RequesterNotFound() {
        doThrow(new IllegalArgumentException("Пользователя, запрашивающего рекомендацию не существует"))
                .when(recommendationRequestValidator).validateUsersExistence(null, receiver);

        when(userService.getUserById(1L)).thenReturn(null);
        when(userService.getUserById(2L)).thenReturn(receiver);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(recommendationRequestDto)
        );

        assertEquals("Пользователя, запрашивающего рекомендацию не существует", exception.getMessage());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).getUserById(2L);
        verify(recommendationRequestValidator, times(1)).validateUsersExistence(null, receiver);
        verify(recommendationRequestValidator, never()).validateRequestFrequency(anyLong(), anyLong());
        verify(skillValidator, never()).validateSkills(anyList());
        verify(recommendationRequestMapper, never()).toEntity(any());
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
    }

    @Test
    void testCreateRecommendationRequest_ReceiverNotFound() {
        doThrow(new IllegalArgumentException("Пользователя, получающего рекомендацию не существует"))
                .when(recommendationRequestValidator).validateUsersExistence(requester, null);

        when(userService.getUserById(1L)).thenReturn(requester);
        when(userService.getUserById(2L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(recommendationRequestDto)
        );

        assertEquals("Пользователя, получающего рекомендацию не существует", exception.getMessage());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).getUserById(2L);
        verify(recommendationRequestValidator, times(1)).validateUsersExistence(requester, null);
        verify(recommendationRequestValidator, never()).validateRequestFrequency(anyLong(), anyLong());
        verify(skillValidator, never()).validateSkills(anyList());
        verify(recommendationRequestMapper, never()).toEntity(any());
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
    }

    @Test
    void testCreateRecommendationRequest_RequestLimitExceeded() {
        when(userService.getUserById(1L)).thenReturn(requester);
        when(userService.getUserById(2L)).thenReturn(receiver);
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.of(RecommendationRequest.builder()
                        .createdAt(LocalDateTime.now().minusMonths(3))
                        .build()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(recommendationRequestDto)
        );

        assertEquals("Запрос этому пользователю можно отправлять только раз в полгода", exception.getMessage());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).getUserById(2L);
        verify(recommendationRequestRepository, times(1))
                .findLatestPendingRequest(1L, 2L);
        verify(skillValidator, never()).validateSkills(anyList());
        verify(recommendationRequestMapper, never()).toEntity(any());
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
        verify(skillRequestService, never()).createSkillRequest(any(), any());
    }

    @Test
    void testCreateRecommendationRequest_SkillsNotFound() {
        when(userService.getUserById(1L)).thenReturn(requester);
        when(userService.getUserById(2L)).thenReturn(receiver);
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.empty());
        doThrow(new IllegalArgumentException("Некоторых скиллов нет в базе данных"))
                .when(skillValidator).validateSkills(recommendationRequestDto.getSkills());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(recommendationRequestDto)
        );

        assertEquals("Некоторых скиллов нет в базе данных", exception.getMessage());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).getUserById(2L);
        verify(recommendationRequestRepository, times(1))
                .findLatestPendingRequest(1L, 2L);
        verify(skillValidator, times(1)).validateSkills(recommendationRequestDto.getSkills());
        verify(recommendationRequestMapper, never()).toEntity(any());
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
        verify(skillRequestService, never()).createSkillRequest(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetRequests_WithAllFilters() {
        RequestFilterDto filter = RequestFilterDto.builder()
                .status(RequestStatus.PENDING)
                .requesterId(1L)
                .receiverId(2L)
                .startDate(LocalDateTime.of(2024, 1, 1, 0, 0))
                .endDate(LocalDateTime.of(2024, 12, 31, 23, 59))
                .skillTitles(Arrays.asList("Java", "Spring"))
                .build();

        RecommendationRequest requestOne = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.of(2024, 8, 21, 12, 0))
                .requester(requester)
                .receiver(receiver)
                .message("Пожалуйста, дай мне рекомендацию")
                .build();

        RecommendationRequestDto dtoOne = RecommendationRequestDto.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.of(2024, 8, 21, 12, 0))
                .requesterId(1L)
                .receiverId(2L)
                .message("Пожалуйста, дай мне рекомендацию")
                .skills(Arrays.asList(1L, 2L))
                .build();

        List<RecommendationRequest> requestsList = Arrays.asList(requestOne);

        when(recommendationRequestRepository.findAll()).thenReturn(requestsList);
        when(filterManager.applyFilters(any(Stream.class), eq(filter))).thenReturn(requestsList.stream());
        when(recommendationRequestMapper.toDto(requestOne)).thenReturn(dtoOne);

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dtoOne.getId(), result.get(0).getId());

        verify(recommendationRequestRepository, times(1)).findAll();
        verify(filterManager, times(1)).applyFilters(any(Stream.class), eq(filter));
        verify(recommendationRequestMapper, times(1)).toDto(requestOne);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetRequests_NoFilters() {
        RecommendationRequest requestOne = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.ACCEPTED)
                .createdAt(LocalDateTime.now())
                .requester(requester)
                .receiver(receiver)
                .message("Пожалуйста, дай мне рекомендацию")
                .build();

        RecommendationRequest requestTwo = RecommendationRequest.builder()
                .id(2L)
                .status(RequestStatus.REJECTED)
                .createdAt(LocalDateTime.now().minusDays(10))
                .requester(requester)
                .receiver(receiver)
                .message("Пожалуйста, дай мне рекомендацию")
                .build();

        RecommendationRequestDto dtoOne = RecommendationRequestDto.builder()
                .id(1L)
                .status(RequestStatus.ACCEPTED)
                .createdAt(requestOne.getCreatedAt())
                .requesterId(1L)
                .receiverId(2L)
                .message("Пожалуйста, дай мне рекомендацию")
                .skills(Arrays.asList(1L, 2L))
                .build();

        RecommendationRequestDto dtoTwo = RecommendationRequestDto.builder()
                .id(2L)
                .status(RequestStatus.REJECTED)
                .createdAt(requestTwo.getCreatedAt())
                .requesterId(1L)
                .receiverId(2L)
                .message("Пожалуйста, дай мне рекомендацию")
                .skills(Arrays.asList(1L, 2L))
                .build();

        List<RecommendationRequest> requestsList = Arrays.asList(requestOne, requestTwo);

        when(recommendationRequestRepository.findAll()).thenReturn(requestsList);
        when(filterManager.applyFilters(any(Stream.class), any(RequestFilterDto.class)))
                .thenReturn(requestsList.stream());
        when(recommendationRequestMapper.toDto(requestOne)).thenReturn(dtoOne);
        when(recommendationRequestMapper.toDto(requestTwo)).thenReturn(dtoTwo);

        RequestFilterDto filter = RequestFilterDto.builder().build();
        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filter);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dtoOne.getId(), result.get(0).getId());
        assertEquals(dtoTwo.getId(), result.get(1).getId());

        verify(recommendationRequestRepository, times(1)).findAll();
        verify(filterManager, times(1)).applyFilters(any(Stream.class), eq(filter));
        verify(recommendationRequestMapper, times(1)).toDto(requestOne);
        verify(recommendationRequestMapper, times(1)).toDto(requestTwo);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetRequests_NoResults() {
        RequestFilterDto filter = RequestFilterDto.builder()
                .status(RequestStatus.REJECTED)
                .build();

        List<RecommendationRequest> requestsList = Arrays.asList();

        when(recommendationRequestRepository.findAll()).thenReturn(requestsList);
        when(filterManager.applyFilters(any(Stream.class), eq(filter))).thenReturn(requestsList.stream());

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filter);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(recommendationRequestRepository, times(1)).findAll();
        verify(filterManager, times(1)).applyFilters(any(Stream.class), eq(filter));
        verify(recommendationRequestMapper, never()).toDto(any());
    }

    @Test
    void getRequest_Success() {
        Long id = 1L;

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto result = recommendationRequestService.getRequest(id);

        assertNotNull(result);
        assertEquals(recommendationRequestDto.getId(), result.getId());
        assertEquals(recommendationRequestDto.getRequesterId(), result.getRequesterId());
        assertEquals(recommendationRequestDto.getReceiverId(), result.getReceiverId());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestMapper, times(1)).toDto(recommendationRequest);
    }


    @Test
    void getRequest_NotFound() {
        Long id = 99L;

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        RecommendationRequestNotFoundException exception =
                assertThrows(RecommendationRequestNotFoundException.class, () ->
                        recommendationRequestService.getRequest(id)
                );

        assertEquals("Запрос на рекомендацию с таким id не найден", exception.getMessage());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestMapper, never()).toDto(any());
    }


    @Test
    void rejectRequest_Success() {
        Long id = 1L;
        RejectionDto rejection = RejectionDto.builder()
                .reason("Просто не хочу. Ты мне неприятен.")
                .build();

        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setRejectionReason(null);

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(recommendationRequest)).thenReturn(recommendationRequest);

        RecommendationRequestDto rejectedDto = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .receiverId(2L)
                .status(RequestStatus.REJECTED)
                .createdAt(recommendationRequest.getCreatedAt())
                .skills(Arrays.asList(1L, 2L, 3L))
                .message("Please give me a recommendation")
                .rejectionReason("Просто не хочу. Ты мне неприятен.")
                .build();

        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(rejectedDto);

        RecommendationRequestDto result = recommendationRequestService.rejectRequest(id, rejection);

        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED, result.getStatus());
        assertEquals("Просто не хочу. Ты мне неприятен.", result.getRejectionReason());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
        verify(recommendationRequestMapper, times(1)).toDto(recommendationRequest);
    }

    @Test
    void rejectRequest_AlreadyRejected() {
        Long id = 1L;
        RejectionDto rejection = RejectionDto.builder()
                .reason("Запрос уже отклонен")
                .build();

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason("Просто не хочу. Ты мне неприятен.");

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                recommendationRequestService.rejectRequest(id, rejection)
        );

        assertEquals("Невозможно отклонить запрос на рекомендацию, поскольку он уже имеет статус REJECTED", exception.getMessage());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
    }

    @Test
    void rejectRequest_NotFound() {
        Long id = 99L;
        RejectionDto rejection = RejectionDto.builder()
                .reason("Запрос на рекомендацию с таким Id не найден")
                .build();

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        RecommendationRequestNotFoundException exception =
                assertThrows(RecommendationRequestNotFoundException.class, () ->
                        recommendationRequestService.rejectRequest(id, rejection)
                );

        assertEquals("Запрос на рекомендацию с таким id не найден", exception.getMessage());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
    }
}


