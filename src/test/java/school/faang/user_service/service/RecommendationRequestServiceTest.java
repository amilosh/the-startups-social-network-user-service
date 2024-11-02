package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
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


}
