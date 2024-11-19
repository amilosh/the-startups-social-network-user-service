package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillValidator skillValidator;

    @Mock
    private UserService userService;

    @Mock
    private UserSkillGuaranteeService userSkillGuaranteeService;

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private User userMock;

    @InjectMocks
    private SkillService skillService;

    private Skill skill;
    private Recommendation recommendation;
    private RecommendationDto dto;

    @BeforeEach
    void setUp() {
        skill = Skill.builder()
                .id(1L)
                .guarantees(List.of(UserSkillGuarantee.builder()
                        .guarantor(User.builder()
                                .id(1L)
                                .build())
                        .build()))
                .build();
        dto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .content("initial content")
                .skillOffers(List.of(SkillOfferDto.builder()
                        .id(1L)
                        .recommendationId(1L)
                        .skillId(1L)
                        .build()))
                .build();

        recommendation = new Recommendation();
    }

    @Test
    void testGetSkillGuaranteeIdsOneGuarantee() {

        List<Long> result = skillService.getSkillGuaranteeIds(skill);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(List.of(1L), result);

        verify(skillValidator, times(1)).validateSkillExists(skill.getId());
    }

    @Test
    void testGetSkillGuaranteeIdsNoGuarantees() {
        skill.setGuarantees(List.of());

        List<Long> result = skillService.getSkillGuaranteeIds(skill);

        assertNotNull(result);
        assertEquals(0, result.size());
        assertEquals(List.of(), result);

        verify(skillValidator, times(1)).validateSkillExists(skill.getId());
    }

    @Test
    void testAddGuarantee() {
        recommendation.setReceiver(User.builder().id(1L).skills(List.of()).build());
        recommendation.setAuthor(User.builder().id(2L).build());
        recommendation.setSkillOffers(List.of(SkillOffer.builder().id(1L).build()));
        when(userService.findUserById(recommendation.getReceiver().getId())).thenReturn(userMock);
        when(userMock.getSkills()).thenReturn(List.of(skill));

        skillService.addGuarantee(recommendation);

        verify(userSkillGuaranteeService, times(1)).addSkillGuarantee(skill, recommendation);
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    void testCheckIfSkillExistsById_SkillExists() {
        Long skillId = 1L;
        when(skillRepository.existsById(skillId)).thenReturn(true);

        boolean exists = skillService.checkIfSkillExistsById(skillId);

        assertThat(exists).isTrue();
        verify(skillRepository, times(1)).existsById(skillId);
    }

    @Test
    void testCheckIfSkillExistsById_SkillDoesNotExist() {
        Long skillId = 1L;
        when(skillRepository.existsById(skillId)).thenReturn(false);

        boolean exists = skillService.checkIfSkillExistsById(skillId);

        assertThat(exists).isFalse();
        verify(skillRepository, times(1)).existsById(skillId);
    }

    @Test
    void testGetSkillById_SkillExists() {
        Long skillId = 1L;
        Skill skill = new Skill();
        skill.setId(skillId);
        skill.setTitle("Test Skill");
        when(skillRepository.getReferenceById(skillId)).thenReturn(skill);

        Skill retrievedSkill = skillService.getSkillById(skillId);

        assertThat(retrievedSkill).isNotNull();
        assertThat(retrievedSkill.getId()).isEqualTo(skillId);
        verify(skillRepository, times(1)).getReferenceById(skillId);
    }

    @Test
    void testGetSkillById_SkillDoesNotExist() {
        Long skillId = 1L;
        when(skillRepository.getReferenceById(skillId)).thenThrow(new EntityNotFoundException());

        assertThatThrownBy(() -> skillService.getSkillById(skillId))
                .isInstanceOf(EntityNotFoundException.class);
        verify(skillRepository, times(1)).getReferenceById(skillId);
    }
}
