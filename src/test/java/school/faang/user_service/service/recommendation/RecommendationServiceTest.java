package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationListMapper;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.mapper.recommendation.RecommendationMapperImpl;
import school.faang.user_service.mapper.recommendation.SkillOfferListMapperImpl;
import school.faang.user_service.mapper.recommendation.SkillOfferMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.recommendation.RecommendationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private RecommendationListMapper recommendationListMapper;
    private static RecommendationMapperImpl recommendationMapperImpl;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptorFirst;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptorSecond;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptorThird;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptorFourth;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    private ArgumentCaptor<PageRequest> pageRequestArgumentCaptor;

    @InjectMocks
    private RecommendationService recommendationService;

    private final long recommendationId = 1L;
    private final long recommendationSecondId = 12L;
    private final long recommendationThirdId = 13L;
    private final long authorId = 2L;
    private final long receiverId = 3L;
    private final long skillFirstId = 4L;
    private final long skillSecondId = 5L;
    private final long skillThirdId = 10L;
    private final long skillOfferFirstId = 6L;
    private final long skillOfferSecondId = 7L;
    private final long requestId = 8L;
    private final String content = "Content";


    @BeforeAll
    public static void setUp() {
        SkillOfferMapperImpl skillOfferMapperImpl = new SkillOfferMapperImpl();
        SkillOfferListMapperImpl skillOfferListMapperImpl = new SkillOfferListMapperImpl(skillOfferMapperImpl);
        recommendationMapperImpl = new RecommendationMapperImpl(skillOfferListMapperImpl);
    }

    @Test
    public void createSuccessTest() {
        RecommendationDto recommendationDto = getRecommendationDto();
        Recommendation recommendation = recommendationMapperImpl.toRecommendation(recommendationDto);

        User author = getAuthor();
        User receiver = getReceiver();

        Skill skillFirst = getSkillFirst();
        Skill skillSecond = getSkillSecond();
        Skill skillThird = getSkillThird();

        List<Skill> userSkills = List.of(
                skillFirst,
                skillSecond
        );

        List<SkillOffer> skillOfferFirstList = getSkillOfferList(skillOfferFirstId, skillSecond);
        List<SkillOffer> skillOfferSecondList = getSkillOfferList(skillOfferSecondId, skillThird);

        Recommendation recFirst = getRecommendation(recommendationSecondId, skillOfferFirstList);
        Recommendation recSecond = getRecommendation(recommendationThirdId, skillOfferSecondList);

        List<Recommendation> authorReceiverRecommendation = List.of(
                recFirst,
                recSecond
        );

        when(userRepository.findById(recommendationDto.getAuthorId())).thenReturn(Optional.of(author));
        when(userRepository.findById(recommendationDto.getReceiverId())).thenReturn(Optional.of(receiver));
        when(recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(),
                recommendationDto.getContent())).thenReturn(recommendationId);
        when(skillOfferRepository.create(skillSecondId, recommendationId)).thenReturn(skillOfferFirstId);
        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(recommendation));

        when(skillRepository.findAllByUserId(recommendationDto.getReceiverId())).thenReturn(userSkills);
        when(recommendationRepository.findAllByAuthorIdAndReceiverIdOrderByCreatedAtDesc(author.getId(), receiver.getId())).thenReturn(authorReceiverRecommendation);
        when(skillRepository.save(skillSecond)).thenReturn(skillSecond);
        when(recommendationMapper.toRecommendationDto(recommendation)).thenReturn(recommendationDto);

        RecommendationDto recommendationDtoActual = recommendationService.createRecommendation(recommendationDto);

        verify(recommendationRepository, times(1)).create(
                longArgumentCaptorFirst.capture(), longArgumentCaptorSecond.capture(), stringArgumentCaptor.capture());
        assertThat(longArgumentCaptorFirst.getValue()).isEqualTo(recommendationDto.getAuthorId());
        assertThat(longArgumentCaptorSecond.getValue()).isEqualTo(recommendationDto.getReceiverId());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(recommendationDto.getContent());
        verify(skillOfferRepository, times(1)).create(
                longArgumentCaptorThird.capture(), longArgumentCaptorFourth.capture());
        assertThat(longArgumentCaptorThird.getValue()).isEqualTo(skillSecondId);
        assertThat(longArgumentCaptorFourth.getValue()).isEqualTo(recommendationId);
        verify(recommendationRepository, times(1)).findById(recommendationId);
        verify(recommendationMapper, times(1)).toRecommendationDto(recommendation);
        verify(userRepository, times(1)).findById(recommendationDto.getAuthorId());
        verify(userRepository, times(1)).findById(recommendationDto.getReceiverId());
        verify(skillRepository, times(1)).findAllByUserId(recommendationDto.getReceiverId());
        verify(recommendationRepository, times(1))
                .findAllByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());

        verify(skillRepository, times(1)).save(skillSecond);
    }

    @Test
    public void createWithoutAuthorFailTest() {
        RecommendationDto recommendationDto = getRecommendationDto();

        when(userRepository.findById(recommendationDto.getAuthorId())).thenReturn(Optional.empty());

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> recommendationService.createRecommendation(recommendationDto)
                );
        verify(userRepository, times(1)).findById(recommendationDto.getAuthorId());
        assertEquals(String.format(RecommendationService.AUTHOR_NOT_FOUND, authorId), dataValidationException.getMessage());
    }

    @Test
    public void createWithoutReceiverFailTest() {
        RecommendationDto recommendationDto = getRecommendationDto();
        User author = getAuthor();

        when(userRepository.findById(recommendationDto.getAuthorId())).thenReturn(Optional.of(author));
        when(userRepository.findById(recommendationDto.getReceiverId())).thenReturn(Optional.empty());

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> recommendationService.createRecommendation(recommendationDto)
                );
        verify(userRepository, times(1)).findById(recommendationDto.getAuthorId());
        verify(userRepository, times(1)).findById(recommendationDto.getReceiverId());
        assertEquals(String.format(RecommendationService.RECEIVER_NOT_FOUND, receiverId), dataValidationException.getMessage());
    }

    @Test
    public void createRecommendationWithGettingRecommendationFailTest() {
        RecommendationDto recommendationDto = getRecommendationDto();

        User author = getAuthor();
        User receiver = getReceiver();

        Skill skillFirst = getSkillFirst();
        Skill skillSecond = getSkillSecond();
        Skill skillThird = getSkillThird();

        List<Skill> userSkills = List.of(
                skillFirst,
                skillSecond
        );

        List<SkillOffer> skillOfferFirstList = getSkillOfferList(skillOfferFirstId, skillSecond);
        List<SkillOffer> skillOfferSecondList = getSkillOfferList(skillOfferSecondId, skillThird);

        Recommendation recFirst = getRecommendation(recommendationSecondId, skillOfferFirstList);
        Recommendation recSecond = getRecommendation(recommendationThirdId, skillOfferSecondList);

        List<Recommendation> authorReceiverRecommendation = List.of(
                recFirst,
                recSecond
        );

        when(userRepository.findById(recommendationDto.getAuthorId())).thenReturn(Optional.of(author));
        when(userRepository.findById(recommendationDto.getReceiverId())).thenReturn(Optional.of(receiver));

        when(recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(),
                recommendationDto.getContent())).thenReturn(recommendationId);
        when(skillOfferRepository.create(skillSecondId, recommendationId)).thenReturn(skillOfferFirstId);
        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.empty());
        when(skillRepository.findAllByUserId(recommendationDto.getReceiverId())).thenReturn(userSkills);
        when(recommendationRepository.findAllByAuthorIdAndReceiverIdOrderByCreatedAtDesc(author.getId(),
                receiver.getId())).thenReturn(authorReceiverRecommendation);
        when(skillRepository.save(skillSecond)).thenReturn(skillSecond);

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> recommendationService.createRecommendation(recommendationDto)
                );

        assertEquals(String.format(RecommendationService.RECOMMENDATION_NOT_FOUND, recommendationId), dataValidationException.getMessage());

        verify(recommendationRepository, times(1)).create(
                longArgumentCaptorFirst.capture(), longArgumentCaptorSecond.capture(), stringArgumentCaptor.capture());
        assertThat(longArgumentCaptorFirst.getValue()).isEqualTo(recommendationDto.getAuthorId());
        assertThat(longArgumentCaptorSecond.getValue()).isEqualTo(recommendationDto.getReceiverId());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(recommendationDto.getContent());
        verify(skillOfferRepository, times(1)).create(
                longArgumentCaptorThird.capture(), longArgumentCaptorFourth.capture());
        assertThat(longArgumentCaptorThird.getValue()).isEqualTo(skillSecondId);
        assertThat(longArgumentCaptorFourth.getValue()).isEqualTo(recommendationId);
        verify(recommendationRepository, times(1)).findById(recommendationId);
        verify(userRepository, times(1)).findById(recommendationDto.getAuthorId());
        verify(userRepository, times(1)).findById(recommendationDto.getReceiverId());
        verify(skillRepository, times(1)).findAllByUserId(recommendationDto.getReceiverId());
        verify(recommendationRepository, times(1))
                .findAllByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        verify(skillRepository, times(1)).save(skillSecond);
    }

    @Test
    public void updateRecommendationSuccessTest() {
        RecommendationDto recommendationDto = getRecommendationDto();
        Recommendation recommendation = recommendationMapperImpl.toRecommendation(recommendationDto);

        User author = getAuthor();
        User receiver = getReceiver();

        Skill skillFirst = getSkillFirst();
        Skill skillSecond = getSkillSecond();
        Skill skillThird = getSkillThird();

        List<Skill> userSkills = List.of(
                skillFirst,
                skillSecond
        );

        List<SkillOffer> skillOfferFirstList = getSkillOfferList(skillOfferFirstId, skillSecond);
        List<SkillOffer> skillOfferSecondList = getSkillOfferList(skillOfferSecondId, skillThird);

        Recommendation recFirst = getRecommendation(recommendationSecondId, skillOfferFirstList);
        Recommendation recSecond = getRecommendation(recommendationThirdId, skillOfferSecondList);

        List<Recommendation> authorReceiverRecommendation = List.of(
                recFirst,
                recSecond
        );

        when(userRepository.findById(recommendationDto.getAuthorId())).thenReturn(Optional.of(author));
        when(userRepository.findById(recommendationDto.getReceiverId())).thenReturn(Optional.of(receiver));
        doNothing().when(recommendationRepository).update(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        doNothing().when(skillOfferRepository).deleteAllByRecommendationId(recommendationDto.getId());
        when(skillOfferRepository.create(skillSecondId, recommendationId)).thenReturn(skillOfferFirstId);
        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(recommendation));
        when(skillRepository.findAllByUserId(recommendationDto.getReceiverId())).thenReturn(userSkills);
        when(recommendationRepository.findAllByAuthorIdAndReceiverIdOrderByCreatedAtDesc(author.getId(), receiver.getId())).thenReturn(authorReceiverRecommendation);
        when(skillRepository.save(skillSecond)).thenReturn(skillSecond);
        when(recommendationMapper.toRecommendationDto(recommendation)).thenReturn(recommendationDto);

        RecommendationDto recommendationDtoActual = recommendationService.updateRecommendation(recommendationDto);

        verify(recommendationRepository, times(1)).update(
                longArgumentCaptorFirst.capture(), longArgumentCaptorSecond.capture(), stringArgumentCaptor.capture());
        assertThat(longArgumentCaptorFirst.getValue()).isEqualTo(recommendationDto.getAuthorId());
        assertThat(longArgumentCaptorSecond.getValue()).isEqualTo(recommendationDto.getReceiverId());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(recommendationDto.getContent());
        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(recommendationDto.getId());
        verify(skillOfferRepository, times(1)).create(
                longArgumentCaptorThird.capture(), longArgumentCaptorFourth.capture());
        assertThat(longArgumentCaptorThird.getValue()).isEqualTo(skillSecondId);
        assertThat(longArgumentCaptorFourth.getValue()).isEqualTo(recommendationId);
        verify(recommendationRepository, times(1)).findById(recommendationId);
        verify(recommendationMapper, times(1)).toRecommendationDto(recommendation);
        verify(userRepository, times(1)).findById(recommendationDto.getAuthorId());
        verify(userRepository, times(1)).findById(recommendationDto.getReceiverId());
        verify(skillRepository, times(1)).findAllByUserId(recommendationDto.getReceiverId());
        verify(recommendationRepository, times(1))
                .findAllByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());

        verify(skillRepository, times(1)).save(skillSecond);
    }

    @Test
    public void updateRecommendationWithGettingRecommendationFailTest() {
        RecommendationDto recommendationDto = getRecommendationDto();

        User author = getAuthor();
        User receiver = getReceiver();

        Skill skillFirst = getSkillFirst();
        Skill skillSecond = getSkillSecond();
        Skill skillThird = getSkillThird();

        List<Skill> userSkills = List.of(
                skillFirst,
                skillSecond
        );

        List<SkillOffer> skillOfferFirstList = getSkillOfferList(skillOfferFirstId, skillSecond);
        List<SkillOffer> skillOfferSecondList = getSkillOfferList(skillOfferSecondId, skillThird);

        Recommendation recFirst = getRecommendation(recommendationSecondId, skillOfferFirstList);
        Recommendation recSecond = getRecommendation(recommendationThirdId, skillOfferSecondList);

        List<Recommendation> authorReceiverRecommendation = List.of(
                recFirst,
                recSecond
        );

        when(userRepository.findById(recommendationDto.getAuthorId())).thenReturn(Optional.of(author));
        when(userRepository.findById(recommendationDto.getReceiverId())).thenReturn(Optional.of(receiver));
        doNothing().when(recommendationRepository).update(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        doNothing().when(skillOfferRepository).deleteAllByRecommendationId(recommendationDto.getId());
        when(skillOfferRepository.create(skillSecondId, recommendationId)).thenReturn(skillOfferFirstId);
        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.empty());
        when(skillRepository.findAllByUserId(recommendationDto.getReceiverId())).thenReturn(userSkills);
        when(recommendationRepository.findAllByAuthorIdAndReceiverIdOrderByCreatedAtDesc(author.getId(),
                receiver.getId())).thenReturn(authorReceiverRecommendation);
        when(skillRepository.save(skillSecond)).thenReturn(skillSecond);

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> recommendationService.updateRecommendation(recommendationDto)
                );

        assertEquals(String.format(RecommendationService.RECOMMENDATION_NOT_FOUND, recommendationId), dataValidationException.getMessage());
        verify(recommendationRepository, times(1)).update(
                longArgumentCaptorFirst.capture(), longArgumentCaptorSecond.capture(), stringArgumentCaptor.capture());
        assertThat(longArgumentCaptorFirst.getValue()).isEqualTo(recommendationDto.getAuthorId());
        assertThat(longArgumentCaptorSecond.getValue()).isEqualTo(recommendationDto.getReceiverId());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(recommendationDto.getContent());
        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(recommendationDto.getId());
        verify(skillOfferRepository, times(1)).create(
                longArgumentCaptorThird.capture(), longArgumentCaptorFourth.capture());
        assertThat(longArgumentCaptorThird.getValue()).isEqualTo(skillSecondId);
        assertThat(longArgumentCaptorFourth.getValue()).isEqualTo(recommendationId);
        verify(recommendationRepository, times(1)).findById(recommendationId);
        verify(userRepository, times(1)).findById(recommendationDto.getAuthorId());
        verify(userRepository, times(1)).findById(recommendationDto.getReceiverId());
        verify(skillRepository, times(1)).findAllByUserId(recommendationDto.getReceiverId());
        verify(recommendationRepository, times(1))
                .findAllByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());

        verify(skillRepository, times(1)).save(skillSecond);
    }

    @Test
    public void updateRecommendationWithoutAuthorFailTest() {
        RecommendationDto recommendationDto = getRecommendationDto();

        when(userRepository.findById(recommendationDto.getAuthorId())).thenReturn(Optional.empty());

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> recommendationService.updateRecommendation(recommendationDto)
                );
        verify(userRepository, times(1)).findById(recommendationDto.getAuthorId());
        assertEquals(String.format(RecommendationService.AUTHOR_NOT_FOUND, authorId), dataValidationException.getMessage());
    }

    @Test
    public void updateRecommendationWithoutReceiverFailTest() {
        RecommendationDto recommendationDto = getRecommendationDto();
        User author = getAuthor();

        when(userRepository.findById(recommendationDto.getAuthorId())).thenReturn(Optional.of(author));
        when(userRepository.findById(recommendationDto.getReceiverId())).thenReturn(Optional.empty());

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> recommendationService.updateRecommendation(recommendationDto)
                );
        verify(userRepository, times(1)).findById(recommendationDto.getAuthorId());
        verify(userRepository, times(1)).findById(recommendationDto.getReceiverId());
        assertEquals(String.format(RecommendationService.RECEIVER_NOT_FOUND, receiverId), dataValidationException.getMessage());
    }

    @Test
    public void deleteRecommendationSuccessTest() {
        doNothing().when(recommendationValidator).validateRecommendationExist(recommendationId);
        doNothing().when(recommendationRepository).deleteById(recommendationId);

        recommendationService.deleteRecommendation(recommendationId);

        verify(recommendationValidator, times(1)).validateRecommendationExist(longArgumentCaptorFirst.capture());
        assertThat(longArgumentCaptorFirst.getValue()).isEqualTo(recommendationId);
        verify(recommendationRepository, times(1)).deleteById(longArgumentCaptorSecond.capture());
        assertThat(longArgumentCaptorSecond.getValue()).isEqualTo(recommendationId);
    }

    @Test
    public void getAllUserRecommendationsSuccessTest() {
        PageRequest pageRequest = PageRequest.of(0, 200);
        List<Recommendation> recommendationList = List.of(Recommendation.builder().build());
        Page<Recommendation> pagedList = new PageImpl<>(recommendationList);

        doNothing().when(recommendationValidator).validateReceiverExist(receiverId);
        when(recommendationRepository.findAllByReceiverId(receiverId, pageRequest)).thenReturn(pagedList);
        when(recommendationListMapper.toRecommendationDtoList(pagedList.getContent())).thenReturn(List.of(new RecommendationDto()));

        recommendationService.getAllUserRecommendations(receiverId);

        verify(recommendationValidator, times(1)).validateReceiverExist(longArgumentCaptorFirst.capture());
        assertThat(longArgumentCaptorFirst.getValue()).isEqualTo(receiverId);
        verify(recommendationRepository, times(1)).findAllByReceiverId(longArgumentCaptorSecond.capture(), pageRequestArgumentCaptor.capture());
        assertThat(longArgumentCaptorSecond.getValue()).isEqualTo(receiverId);
        verify(recommendationListMapper, times(1)).toRecommendationDtoList(pagedList.getContent());
    }

    @Test
    public void getAllGivenRecommendationsSuccessTest() {
        PageRequest pageRequest = PageRequest.of(0, 200);
        List<Recommendation> recommendationList = List.of(Recommendation.builder().build());
        Page<Recommendation> pagedList = new PageImpl<>(recommendationList);

        doNothing().when(recommendationValidator).validateAuthorExist(authorId);
        when(recommendationRepository.findAllByAuthorId(authorId, pageRequest)).thenReturn(pagedList);
        when(recommendationListMapper.toRecommendationDtoList(pagedList.getContent())).thenReturn(List.of(new RecommendationDto()));

        recommendationService.getAllGivenRecommendations(authorId);

        verify(recommendationValidator, times(1)).validateAuthorExist(longArgumentCaptorFirst.capture());
        assertThat(longArgumentCaptorFirst.getValue()).isEqualTo(authorId);
        verify(recommendationRepository, times(1)).findAllByAuthorId(longArgumentCaptorSecond.capture(), pageRequestArgumentCaptor.capture());
        assertThat(longArgumentCaptorSecond.getValue()).isEqualTo(authorId);
        verify(recommendationListMapper, times(1)).toRecommendationDtoList(pagedList.getContent());
    }

    private RecommendationDto getRecommendationDto() {
        SkillOfferDto skillOfferDto = new SkillOfferDto(20L, skillSecondId, recommendationId);
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(recommendationId);
        recommendationDto.setAuthorId(authorId);
        recommendationDto.setReceiverId(receiverId);
        recommendationDto.setRequestId(requestId);
        recommendationDto.setContent(content);
        recommendationDto.setSkillOffers(List.of(skillOfferDto));
        return recommendationDto;
    }

    private User getAuthor() {
        return User.builder()
                .id(authorId)
                .build();
    }

    private User getReceiver() {
        return User.builder()
                .id(receiverId)
                .build();
    }

    private Skill getSkillFirst() {
        return Skill.builder()
                .id(skillFirstId)
                .guarantees(new ArrayList<>())
                .build();
    }

    private Skill getSkillSecond() {
        return Skill.builder()
                .id(skillSecondId)
                .guarantees(new ArrayList<>())
                .build();
    }

    private Skill getSkillThird() {
        return Skill.builder()
                .id(skillThirdId)
                .guarantees(new ArrayList<>())
                .build();
    }

    private List<SkillOffer> getSkillOfferList(Long skillOfferId, Skill skill) {
        return List.of(
                SkillOffer.builder()
                        .id(skillOfferId)
                        .skill(skill)
                        .build());
    }

    private Recommendation getRecommendation(Long recommendationId, List<SkillOffer> skillOfferList) {
        return Recommendation.builder()
                .id(recommendationId)
                .skillOffers(skillOfferList)
                .build();
    }
}