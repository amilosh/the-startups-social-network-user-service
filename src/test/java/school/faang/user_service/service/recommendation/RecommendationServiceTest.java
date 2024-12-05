package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    private static final String EXCEPTION_MESSAGE_RECEIVER_NOT_FOUND = "Receiver not found";
    private static final String EXCEPTION_MESSAGE_GUARANTOR_NOT_FOUND = "Guarantor not found";
    private static final long CREATED_RECOMMENDATION_ID = 1L;
    private static final long AUTHOR_ID = 1L;
    private static final long RECEIVER_ID = 2L;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferService skillOfferService;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private UserService userService;
    @Mock
    private RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;
    @InjectMocks
    private RecommendationService recommendationService;

    private RecommendationDto recommendationDto;

    @BeforeEach
    public void setUp() {
        recommendationDto = getRecommendationDto();
    }

    @Test
    void testCreateWithNonExistentReceiver() {
        when(userService.findById(RECEIVER_ID)).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            recommendationService.create(recommendationDto);
        });
        assertEquals(EXCEPTION_MESSAGE_RECEIVER_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testCreateWithNonExistentGuarantor() {
        User receiver = new User();
        receiver.setId(RECEIVER_ID);
        when(userService.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(userService.findById(AUTHOR_ID)).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals(EXCEPTION_MESSAGE_GUARANTOR_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testCreateRecommendationCreated() {
        when(userService.findById(recommendationDto.getAuthorId()))
                .thenReturn(Optional.of(User.builder().id(AUTHOR_ID).build()));
        when(userService.findById(recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(User.builder().id(RECEIVER_ID).build()));
        when(recommendationRepository
                .create(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId(),
                        recommendationDto.getContent()))
                .thenReturn(CREATED_RECOMMENDATION_ID);

        RecommendationDto recommendationDtoWithId = recommendationService.create(recommendationDto);
        RecommendationReceivedEvent event = new RecommendationReceivedEvent(
                recommendationDtoWithId.getId(),
                recommendationDtoWithId.getAuthorId(),
                recommendationDtoWithId.getReceiverId(),
                recommendationDtoWithId.getContent(),
                recommendationDtoWithId.getCreatedAt());

        verify(recommendationRepository).create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        verify(recommendationReceivedEventPublisher).publish(event);
        assertEquals(CREATED_RECOMMENDATION_ID, recommendationDtoWithId.getId());
    }

    @Test
    void testCreateSavedSkillOffers() {
        when(userService.findById(recommendationDto.getAuthorId()))
                .thenReturn(Optional.of(User.builder().id(AUTHOR_ID).build()));
        when(userService.findById(recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(User.builder().id(RECEIVER_ID).build()));
        when(recommendationRepository
                .create(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId(),
                        recommendationDto.getContent()))
                .thenReturn(CREATED_RECOMMENDATION_ID);

        recommendationService.create(recommendationDto);

        verify(skillOfferService).saveSkillOffers(recommendationDto.getSkillOffers(), CREATED_RECOMMENDATION_ID);
    }

    @Test
    void testCreateCreatedGuarantees() {
        User guarantor = User.builder().id(AUTHOR_ID).build();
        User receiver = User.builder().id(RECEIVER_ID).build();
        when(userService.findById(recommendationDto.getAuthorId())).thenReturn(Optional.of(guarantor));
        when(userService.findById(recommendationDto.getReceiverId())).thenReturn(Optional.of(receiver));
        when(skillRepository.findAllByUserId(receiver.getId()))
                .thenReturn(recommendationDto.getSkillOffers().stream()
                        .map(offer -> Skill.builder().id(offer.getSkillId()).guarantees(new ArrayList<>())
                                .build()).toList());

        recommendationService.create(recommendationDto);

        verify(userSkillGuaranteeRepository, times(2))
                .save(any());
    }


    @Test
    void testUpdateUpdatedRecommendation() {
        when(userService.findById(recommendationDto.getAuthorId()))
                .thenReturn(Optional.of(User.builder().id(AUTHOR_ID).build()));
        when(userService.findById(recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(User.builder().id(RECEIVER_ID).build()));

        recommendationService.update(recommendationDto);

        verify(recommendationRepository).update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
    }

    @Test
    void testUpdateCreatedGuarantees() {
        User guarantor = User.builder().id(AUTHOR_ID).build();
        User receiver = User.builder().id(RECEIVER_ID).build();
        when(userService.findById(recommendationDto.getAuthorId())).thenReturn(Optional.of(guarantor));
        when(userService.findById(recommendationDto.getReceiverId())).thenReturn(Optional.of(receiver));
        when(skillRepository.findAllByUserId(receiver.getId()))
                .thenReturn(recommendationDto.getSkillOffers().stream()
                        .map(offer -> Skill.builder().id(offer.getSkillId()).guarantees(new ArrayList<>())
                                .build()).toList());

        recommendationService.update(recommendationDto);

        verify(userSkillGuaranteeRepository, times(2))
                .save(any());
    }


    @Test
    void testDelete() {
        long id = 1L;
        recommendationService.delete(id);
        verify(recommendationRepository).deleteById(id);
    }

    @Test
    void testGetAllUserRecommendations() {
        long userId = 1L;
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<Recommendation> recommendations = List.of(
                Recommendation.builder().id(1L).build(),
                Recommendation.builder().id(2L).build(),
                Recommendation.builder().id(3L).build()
        );
        when(recommendationRepository.findAllByReceiverId(userId, pageable)).thenReturn(
                new PageImpl<>(recommendations, PageRequest.of(page, size), recommendations.size()));

        List<RecommendationDto> expected = recommendations.stream()
                .map(recommendation -> recommendationMapper.toDto(recommendation)).toList();

        List<RecommendationDto> actual = recommendationService.getAllUserRecommendations(userId, page, size);
        assertEquals(actual.size(), expected.size());
        expected.forEach(dto -> assertTrue(actual.contains(dto)));
    }

    @Test
    void testGetAllGivenRecommendations() {
        long userId = 1L;
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<Recommendation> recommendations = List.of(
                Recommendation.builder().id(1L).build(),
                Recommendation.builder().id(2L).build(),
                Recommendation.builder().id(3L).build()
        );
        when(recommendationRepository.findAllByAuthorId(userId, pageable)).thenReturn(
                new PageImpl<>(recommendations, PageRequest.of(page, size), recommendations.size()));

        List<RecommendationDto> expected = recommendations.stream()
                .map(recommendation -> recommendationMapper.toDto(recommendation)).toList();

        List<RecommendationDto> actual = recommendationService.getAllGivenRecommendations(userId, page, size);
        assertEquals(actual.size(), expected.size());
        expected.forEach(dto -> assertTrue(actual.contains(dto)));
    }

    private RecommendationDto getRecommendationDto() {
        return RecommendationDto.builder()
                .id(1L)
                .authorId(AUTHOR_ID)
                .receiverId(RECEIVER_ID)
                .skillOffers(List.of(
                        SkillOfferDto.builder().skillId(1L).build(),
                        SkillOfferDto.builder().skillId(2L).build()
                ))
                .content("Content")
                .build();
    }
}