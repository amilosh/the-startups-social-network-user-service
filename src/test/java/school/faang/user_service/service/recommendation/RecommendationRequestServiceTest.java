package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private List<RecommendationRequestFilter> filters = Collections.emptyList();
    @Captor
    private ArgumentCaptor<RecommendationRequest> recommendationRequestCaptor;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }

    private RecommendationRequestDto createRecommendationRequestDto() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setSkills(List.of(1L, 2L));
        return dto;
    }

    @Test
    void shouldCreateRecommendationRequestSuccessfully() {
        RecommendationRequestDto dto = createRecommendationRequestDto();
        RecommendationRequest requestEntity = new RecommendationRequest();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(skillRepository.countExisting(dto.getSkills())).thenReturn(2);
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenAnswer(invocation -> {
                    RecommendationRequest req = invocation.getArgument(0);
                    req.setId(1L);
                    return req;
                });
        when(skillRepository.getReferenceById(1L)).thenReturn(Skill.builder().id(1L).build());
        when(skillRepository.getReferenceById(2L)).thenReturn(Skill.builder().id(2L).build());

        RecommendationRequestDto createdDto = recommendationRequestService.create(dto);

        assertNotNull(createdDto);
        assertEquals(1L, createdDto.getId());
        assertEquals(RequestStatus.PENDING.toString(), createdDto.getStatus());
        assertEquals(1L, createdDto.getRequesterId());
        assertEquals(2L, createdDto.getReceiverId());
        assertEquals(2, createdDto.getSkills().size());
        assertEquals(1L, createdDto.getSkills().get(0));
        assertEquals(2L, createdDto.getSkills().get(1));

        verify(skillRepository, times(1)).countExisting(dto.getSkills());
        verify(recommendationRequestRepository, times(2)).save(recommendationRequestCaptor.capture());

        List<RecommendationRequest> capturedRequests = recommendationRequestCaptor.getAllValues();
        RecommendationRequest savedEntity = capturedRequests.get(1);

        assertEquals(1L, savedEntity.getRequester().getId());
        assertEquals(2L, savedEntity.getReceiver().getId());
        assertEquals(RequestStatus.PENDING, savedEntity.getStatus());
        assertEquals(2, savedEntity.getSkills().size());
        assertEquals(1L, savedEntity.getSkills().get(0).getSkill().getId());
        assertEquals(2L, savedEntity.getSkills().get(1).getSkill().getId());
    }

    @Test
    void shouldThrowExceptionWhenRequesterNotFound() {
        RecommendationRequestDto dto = createRecommendationRequestDto();

        when(userRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(dto);
        });

        assertEquals("Requester not found", exception.getMessage());
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void shouldThrowExceptionWhenReceiverNotFound() {
        RecommendationRequestDto dto = createRecommendationRequestDto();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(dto);
        });

        assertEquals("Receiver not found", exception.getMessage());
        verify(userRepository, times(1)).existsById(2L);
    }

    @Test
    void shouldThrowExceptionWhenPendingRequestFoundWithinSixMonths() {
        RecommendationRequestDto dto = createRecommendationRequestDto();
        RecommendationRequest existingRequest = new RecommendationRequest();
        existingRequest.setCreatedAt(LocalDateTime.now().minusMonths(2));

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.of(existingRequest));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(dto);
        });

        assertEquals("A recommendation request can only be sent once every 6 months.", exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findLatestPendingRequest(1L, 2L);
    }

    @Test
    void shouldThrowExceptionWhenSkillsDoNotExist() {
        RecommendationRequestDto dto = createRecommendationRequestDto();
        dto.setSkills(List.of(1L, 2L, 3L));

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(skillRepository.countExisting(dto.getSkills())).thenReturn(2);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(dto);
        });

        assertEquals("One or more skills do not exist.", exception.getMessage());
        verify(skillRepository, times(1)).countExisting(dto.getSkills());
    }
}