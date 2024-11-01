package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    private final static int SKILL_ID = 1;
    private final static int USER_ID = 1;

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    SkillOfferRepository skillOfferRepository;

    @Spy
    private SkillMapper skillMapper;

    @Captor
    private ArgumentCaptor<Skill> skillCaptor;

    @Test
    void testCreateWithExistTitle() {
        SkillDto skillDto = existencseCheck(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testCreateWithMissingTitle() {
        SkillDto skillDto = existencseCheck(false);

        skillService.create(skillDto);

        verify(skillRepository, times(1)).save(skillCaptor.capture());
    }

    @Test
    void testGetUserSkills() {
        long userId = 1;

        skillService.getUserSkills(userId);

        verify(skillRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void testGetOfferedSkills() {
        skillRepository.findSkillsOfferedToUser(USER_ID);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(USER_ID);
    }

    @Test
    void testAcquireSkillFromOffersWithExistSkill() {
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.of(new Skill()));

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));
    }

    @Test
    void testAcquireSKillFromOffersSuccess() {
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID))
                .thenReturn(Arrays.asList(new SkillOffer(), new SkillOffer(), new SkillOffer()));
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.of(new Skill()));
        when(skillMapper.toDto(new Skill())).thenReturn(new SkillDto());

        skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        verify(skillRepository, times(1)).assignSkillToUser(SKILL_ID, USER_ID);
    }

    private SkillDto existencseCheck(boolean isExestence) {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("title");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(isExestence);
        return skillDto;
    }
}
