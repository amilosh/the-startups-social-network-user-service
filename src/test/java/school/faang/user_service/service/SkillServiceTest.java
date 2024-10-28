package school.faang.user_service.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillService skillService;

    @BeforeEach
    void setUp() {
        // Initialization, if needed
    }

    @Test
    void getUserSkills_ShouldReturnListOfSkillDtos() {
        long userId = 1L;

        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Java");

        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Python");

        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(skill1, skill2));
        when(skillMapper.toDto(skill1)).thenReturn(new SkillDto(1L, "Java"));
        when(skillMapper.toDto(skill2)).thenReturn(new SkillDto(2L, "Python"));

        List<SkillDto> skills = skillService.getUserSkills(userId);

        assertNotNull(skills, "Skills list should not be null");
        assertEquals(2, skills.size());
        assertEquals("Java", skills.get(0).getTitle());
        assertEquals("Python", skills.get(1).getTitle());
    }

    @Test
    void createSkill_WhenSkillDoesNotExist_ShouldSaveAndReturnDto() {
        SkillDto skillDto = new SkillDto(1L, "Java");

        Skill skill = new Skill();
        skill.setTitle("Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(false);
        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.create(skillDto);

        assertNotNull(result, "Result should not be null");
        assertEquals("Java", result.getTitle());
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    void createSkill_WhenSkillAlreadyExists_ShouldThrowException() {
        SkillDto skillDto = new SkillDto(1L, "Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
        verify(skillRepository, never()).save(any(Skill.class));
    }

    @Test
    void getAllSkills_ShouldReturnListOfSkills() {
        Skill skill1 = new Skill();
        skill1.setTitle("Java");
        Skill skill2 = new Skill();
        skill2.setTitle("Python");

        when(skillRepository.findAll()).thenReturn(List.of(skill1, skill2));
        when(skillMapper.toDto(skill1)).thenReturn(new SkillDto(1L, "Java"));
        when(skillMapper.toDto(skill2)).thenReturn(new SkillDto(2L, "Python"));

        List<SkillDto> skills = skillService.getAllSkills();

        assertNotNull(skills, "Skills list should not be null");
        assertEquals(2, skills.size());
        assertEquals("Java", skills.get(0).getTitle());
        assertEquals("Python", skills.get(1).getTitle());
    }

    @Test
    void getOfferedSkills_ShouldReturnListOfSkillCandidatesWithCounts() {
        long userId = 1L;

        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Java");

        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Python");

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill1, skill1, skill2));
        when(skillMapper.toDto(skill1)).thenReturn(new SkillDto(1L, "Java"));
        when(skillMapper.toDto(skill2)).thenReturn(new SkillDto(2L, "Python"));

        List<SkillCandidateDto> offeredSkills = skillService.getOfferedSkills(userId);

        assertNotNull(offeredSkills, "Offered skills list should not be null");
        assertEquals(2, offeredSkills.size());

        SkillCandidateDto javaSkill = offeredSkills.stream()
                .filter(candidate -> "Java".equals(candidate.getSkill().getTitle()))
                .findFirst()
                .orElse(null);

        SkillCandidateDto pythonSkill = offeredSkills.stream()
                .filter(candidate -> "Python".equals(candidate.getSkill().getTitle()))
                .findFirst()
                .orElse(null);

        assertNotNull(javaSkill, "Java skill should be present");
        assertEquals(2, javaSkill.getOffersAmount());

        assertNotNull(pythonSkill, "Python skill should be present");
        assertEquals(1, pythonSkill.getOffersAmount());
    }
}
