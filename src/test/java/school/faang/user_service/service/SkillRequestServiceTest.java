package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.SkillNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SkillRequestServiceTest {

    private SkillRequestService skillRequestService;
    private SkillRequestRepository skillRequestRepository;
    private SkillRepository skillRepository;

    @BeforeEach
    void setUp() {
        skillRequestRepository = mock(SkillRequestRepository.class);
        skillRepository = mock(SkillRepository.class);
        skillRequestService = new SkillRequestService(skillRequestRepository, skillRepository);
    }

    @Test
    void testCreateSkillRequest_Success() {
        Skill skill = new Skill();
        RecommendationRequest request = new RecommendationRequest();
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setSkill(skill);
        skillRequest.setRequest(request);

        when(skillRequestRepository.save(any(SkillRequest.class))).thenReturn(skillRequest);

        SkillRequest result = skillRequestService.createSkillRequest(skill, request);

        assertNotNull(result);
        assertEquals(skill, result.getSkill());
        assertEquals(request, result.getRequest());
        verify(skillRequestRepository, times(1)).save(any(SkillRequest.class));
    }

    @Test
    void testGetSkillsByIds_Success() {
        List<Long> skillIds = Arrays.asList(1L, 2L, 3L);
        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(2L);
        Skill skill3 = new Skill();
        skill3.setId(3L);
        List<Skill> skills = Arrays.asList(skill1, skill2, skill3);

        when(skillRepository.findAllById(skillIds)).thenReturn(skills);

        List<Skill> result = skillRequestService.getSkillsByIds(skillIds);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(skills));
        verify(skillRepository, times(1)).findAllById(skillIds);
    }

    @Test
    void testGetSkillById_SkillFound() {
        Long skillId = 1L;
        Skill skill = new Skill();
        skill.setId(skillId);

        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));

        Skill result = skillRequestService.getSkillById(skillId);

        assertNotNull(result);
        assertEquals(skillId, result.getId());
        verify(skillRepository, times(1)).findById(skillId);
    }

    @Test
    void testGetSkillById_SkillNotFound() {
        Long skillId = 1L;

        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        SkillNotFoundException exception = assertThrows(SkillNotFoundException.class, () ->
                skillRequestService.getSkillById(skillId)
        );

        assertEquals("Skill with ID 1 not found.", exception.getMessage());
        verify(skillRepository, times(1)).findById(skillId);
    }

}
