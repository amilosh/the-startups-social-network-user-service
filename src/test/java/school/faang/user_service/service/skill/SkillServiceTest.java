package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillCandidateMapper skillCandidateMapper;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private SkillValidator skillValidator;
    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    private SkillDto skillDto;
    private final long skillId = 4L;
    private final long userId = 3L;

    @Test
    public void testCreateWithExistingTitle() {
        skillDto = prepareData(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testSkillWasSaved() {
        skillDto = prepareData(false);
        skillService.create(skillDto);
        Skill skill = skillMapper.toEntity(skillDto);

        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    public void testCheckUserSkills() {
        skillService.getUserSkills(userId);

        verify(skillRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void testCheckOfferedSkills() {
        skillService.getOfferedSkills(userId);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    public void testAcquireSkillSuccess() {
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        Skill skill = new Skill();
        skill.setId(skillId);
        SkillOffer offer = new SkillOffer();
        offer.setSkill(skill);
        offer.setRecommendation(new Recommendation());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(List.of(offer));
        doNothing().when(skillValidator).validateSkillByMinSkillOffer(1, skillId, userId);
        when(skillMapper.toDto(skill)).thenReturn(new SkillDto(skillId, "Skill"));

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertEquals(skillId, result.getId());
        verify(skillRepository).assignSkillToUser(skillId, userId);
        verify(userSkillGuaranteeRepository).saveAll(anyList());
    }

    @Test
    public void testGetOfferedSkill() {
        List<Skill> skills = Arrays.asList(new Skill(), new Skill(), new Skill());
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skills);

        skillService.getOfferedSkills(userId);

        for (Skill skill : skills) {
            verify(skillCandidateMapper, times(skills.size())).toDto(skill);
        }
    }

    private SkillDto prepareData(boolean existsByTitle) {
        SkillDto skillDto = new SkillDto(5L, "Loyalty");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(existsByTitle);

        return skillDto;
    }
}
