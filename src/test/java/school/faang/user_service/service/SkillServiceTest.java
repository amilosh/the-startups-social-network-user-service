package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;
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
    private SkillValidator skillValidator;

    @Spy
    private SkillMapperImpl skillMapper;

    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skillDto = new SkillDto();
        skillDto.setTitle("title");
    }

    @Test
    void testCreateThrowsExceptionWhenTitleExists() {
        doThrow(DataValidationException.class).when(skillValidator).validateExistTitle(skillDto.getTitle());

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testCreateWithMissingTitle() {
        doNothing().when(skillValidator).validateExistTitle(skillDto.getTitle());

        skillService.create(skillDto);

        verify(skillRepository, times(1)).save(skillMapper.dtoToEntity(skillDto));
    }

    @Test
    void testGetUserSkills() {
        skillService.getUserSkills(USER_ID);

        verify(skillRepository, times(1)).findAllByUserId(USER_ID);
    }

    @Test
    void testGetOfferedSkills() {
        List<Skill> skills = List.of(new Skill(), new Skill());
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(skills);

        skillService.getOfferedSkills(USER_ID);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(USER_ID);
    }

    @Test
    void testAcquireSkillFromOffersWithExistSkill() {
        doThrow(DataValidationException.class).when(skillValidator).validateUserSkillExist(SKILL_ID, USER_ID);

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));
    }

    @Test
    void testAcquireSkillFromOffersNumberOffersLessThree() {
        doThrow(DataValidationException.class).when(skillValidator).validateSkillOfferCount(SKILL_ID, USER_ID);

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));
    }

    @Test
    void testAcquireSKillFromOffersSuccess() {
        Skill skill = new Skill();
        doNothing().when(skillValidator).validateUserSkillExist(SKILL_ID, USER_ID);
        doNothing().when(skillValidator).validateSkillOfferCount(SKILL_ID, USER_ID);
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.of(new Skill()));
        when(skillMapper.entityToDto(skill)).thenReturn(new SkillDto());

        skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        verify(skillRepository, times(1)).assignSkillToUser(SKILL_ID, USER_ID);
    }
}
