package school.faang.user_service.service;

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
import school.faang.user_service.validation.skill.SkillValidation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillValidation skillValidation;

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

        verify(skillValidation, times(1)).validateSkillExists(skill.getId());
    }

    @Test
    void testGetSkillGuaranteeIdsNoGuarantees() {
        skill.setGuarantees(List.of());

        List<Long> result = skillService.getSkillGuaranteeIds(skill);

        assertNotNull(result);
        assertEquals(0, result.size());
        assertEquals(List.of(), result);

        verify(skillValidation, times(1)).validateSkillExists(skill.getId());
    }

    @Test
    void testAddGuarantee() {
        recommendation.setReceiver(User.builder().id(1L).skills(List.of()).build());
        recommendation.setAuthor(User.builder().id(2L).build());
        recommendation.setSkillOffers(List.of(SkillOffer.builder().id(1L).build()));
        when(userService.findUser(recommendation.getReceiver().getId())).thenReturn(userMock);
        when(userMock.getSkills()).thenReturn(List.of(skill));

        skillService.addGuarantee(recommendation);

        verify(userSkillGuaranteeService, times(1)).addSkillGuarantee(skill, recommendation);
        verify(skillRepository, times(1)).save(skill);
    }
}
