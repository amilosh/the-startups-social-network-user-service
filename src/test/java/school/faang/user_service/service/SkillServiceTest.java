package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.SkillAlreadyAcquiredException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    private static final long USER_ID = 1L;
    private static final long SKILL_ID = 1L;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private SkillService skillService;

    @BeforeEach
    void setUp() {
        // Mock minOffersRequired value
        ReflectionTestUtils.setField(skillService, "minOffersRequired", 3);
    }

    @Test
    void acquireSkillFromOffers_WhenUserAlreadyHasSkill_ShouldThrowSkillAlreadyAcquiredException() {
        // Mock existing skill for the user
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.of(new Skill()));

        // Assert exception is thrown
        assertThrows(SkillAlreadyAcquiredException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));

        // Verify no skill assignment or guarantee saving happens
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, never()).save(any(UserSkillGuarantee.class));
    }

    @Test
    void acquireSkillFromOffers_WhenNotEnoughOffers_ShouldThrowDataValidationException() {
        // Mock data
        Skill skill = new Skill();
        Recommendation recommendation = new Recommendation();
        recommendation.setReceiver(new User());
        recommendation.setAuthor(new User());

        SkillOffer offer1 = SkillOffer.builder()
                .skill(skill)
                .recommendation(recommendation)
                .build();

        // Mock skill offers less than the required amount
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(List.of(offer1));

        // Assert exception is thrown
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));
        assertEquals("Not enough offers to acquire the skill.", exception.getMessage());

        // Verify no skill assignment or guarantee saving happens
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, never()).save(any(UserSkillGuarantee.class));
    }

    @Test
    void acquireSkillFromOffers_WhenEnoughOffers_ShouldAssignSkillAndSaveGuarantors() {
        // Set up mock data
        Skill skill = new Skill();
        skill.setId(SKILL_ID);
        skill.setTitle("Java");

        User receiver = new User();
        receiver.setId(USER_ID);

        User guarantor1 = new User();
        guarantor1.setId(2L);

        User guarantor2 = new User();
        guarantor2.setId(3L);

        User guarantor3 = new User();
        guarantor3.setId(4L);

        Recommendation recommendation1 = Recommendation.builder()
                .receiver(receiver)
                .author(guarantor1)
                .build();

        Recommendation recommendation2 = Recommendation.builder()
                .receiver(receiver)
                .author(guarantor2)
                .build();

        Recommendation recommendation3 = Recommendation.builder()
                .receiver(receiver)
                .author(guarantor3)
                .build();

        SkillOffer offer1 = SkillOffer.builder()
                .skill(skill)
                .recommendation(recommendation1)
                .build();

        SkillOffer offer2 = SkillOffer.builder()
                .skill(skill)
                .recommendation(recommendation2)
                .build();

        SkillOffer offer3 = SkillOffer.builder()
                .skill(skill)
                .recommendation(recommendation3)
                .build();

        // Mock behavior
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(List.of(offer1, offer2, offer3));
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(skillMapper.toDto(skill)).thenReturn(new SkillDto(SKILL_ID, "Java"));

        // Capture arguments for skill assignment
        ArgumentCaptor<Long> skillIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);

        // Call the service
        SkillDto result = skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        // Verify skill assignment
        verify(skillRepository).assignSkillToUser(skillIdCaptor.capture(), userIdCaptor.capture());
        assertEquals(SKILL_ID, skillIdCaptor.getValue());
        assertEquals(USER_ID, userIdCaptor.getValue());

        // Assert result
        assertNotNull(result, "SkillDto should not be null if the skill acquisition is successful.");
        assertEquals("Java", result.getTitle());

        // Verify guarantors were saved
        verify(userSkillGuaranteeRepository, times(3)).save(any(UserSkillGuarantee.class));
    }

    @Test
    void createSkill_WhenSkillDoesNotExist_ShouldSaveAndReturnDto() {
        SkillDto skillDto = new SkillDto(1L, "Java");

        Skill skill = new Skill();
        skill.setTitle("Java");

        // Mock behavior
        when(skillRepository.existsByTitle("Java")).thenReturn(false);
        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        // Call the service
        SkillDto result = skillService.create(skillDto);

        // Assert the result
        assertNotNull(result, "Result should not be null");
        assertEquals("Java", result.getTitle());
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    void createSkill_WhenSkillAlreadyExists_ShouldThrowException() {
        SkillDto skillDto = new SkillDto(1L, "Java");

        // Mock behavior
        when(skillRepository.existsByTitle("Java")).thenReturn(true);

        // Assert exception
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
        verify(skillRepository, never()).save(any(Skill.class));
    }
}
