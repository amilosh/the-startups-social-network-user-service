package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.web.dto.mapper.SkillMapperImpl;
import school.faang.user_service.web.dto.skill.SkillCandidateDto;
import school.faang.user_service.web.dto.skill.SkillDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillServiceImpl skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Spy
    private SkillMapperImpl skillMapper;

    private SkillDto skillDto;
    private Skill skill;

    @BeforeEach
    void setup() {
        skillDto = new SkillDto();
        skillDto.setTitle("Java Programming");

        skill = new Skill();
        skill.setTitle("Java Programming");
    }

    @Test
    void testCreateSkillSuccessfully() {
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillRepository.saveAndFlush(skill)).thenReturn(skill);

        SkillDto result = skillService.create(skillDto);

        verify(skillRepository,
                        Mockito.times(1))
                .saveAndFlush(skill);
        assertNotNull(result);
        assertEquals(skillDto.getTitle(), result.getTitle());
    }

    @Test
    void testCreateWithTitleAlreadyExists() {
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillService.create(skillDto));
        assertEquals("Skill with this title already exists", exception.getMessage());
    }

    @Test
    void testGetUserSkillsSuccessfully() {
        long userId = 1L;
        List<Skill> skills = List.of(skill);
        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);

        Optional<List<SkillDto>> result = skillService.getUserSkills(userId);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(skillDto.getTitle(), result.get().get(0).getTitle());
    }

    @Test
    void testGetUserSkillsWhenNoSkills() {
        long userId = 1L;
        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of());

        Optional<List<SkillDto>> result = skillService.getUserSkills(userId);

        assertTrue(result.isPresent());
        assertTrue(result.get().isEmpty());
    }

    @Test
    void testGetOfferedSkills() {
        long userId = 1L;
        List<Skill> skills = List.of(skill);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skills);

        Optional<List<SkillCandidateDto>> result = skillService.getOfferedSkills(userId);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        verify(skillMapper).toCandidateDto(skill, 1L);
    }

    @Test
    void testAcquireSkillSuccessfully() {
        long skillId = 1L;
        long userId = 2L;

        Recommendation recommendation = new Recommendation();
        recommendation.setReceiver(new User());
        recommendation.setAuthor(new User());

        SkillOffer offer = new SkillOffer();
        offer.setSkill(skill);
        offer.setRecommendation(recommendation);

        List<SkillOffer> offers = List.of(offer, offer, offer);

        when(skillRepository.findUserSkill(userId, skillId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertNotNull(result);
        assertEquals(skillDto.getTitle(), result.getTitle());
        verify(skillRepository).assignSkillToUser(skillId, userId);
        verify(userSkillGuaranteeRepository).saveAll(anyList());
    }

    @Test
    void testWhenSkillAlreadyAcquired() {
        long skillId = 1L;
        long userId = 2L;
        when(skillRepository.findUserSkill(userId, skillId)).thenReturn(Optional.of(skill));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(skillId, userId));
        assertEquals("User already possesses this skill.", exception.getMessage());
    }

    @Test
    void testAcquireWhenNotEnoughOffers() {
        long skillId = 1L;
        long userId = 2L;
        List<SkillOffer> offers = List.of(new SkillOffer());

        when(skillRepository.findUserSkill(userId, skillId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(skillId, userId));
        assertEquals("Not enough offers to acquire this skill.", exception.getMessage());
    }
}
