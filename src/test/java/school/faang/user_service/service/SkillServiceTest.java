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
import school.faang.user_service.dto.skill.SkillAcquireDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.SkillResourceNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.SkillValidator;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

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

    @Mock
    private SkillOfferService skillOfferService;

    @InjectMocks
    private SkillService skillService;

    private Skill skill;
    private SkillDto skillDto;
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

        skillDto = SkillDto.builder()
                .id(1L)
                .title("title")
                .createdAt(LocalDateTime.now(ZoneId.of("UTC")))
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

    @Test
    void testCreateSkillSuccess() {
        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        doNothing().when(skillValidator).validateDuplicate(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.create(skillDto);

        verify(skillRepository, times(1)).save(skill);
        verify(skillMapper, times(1)).toEntity(skillDto);
        verify(skillMapper, times(1)).toDto(skill);
        assertEquals(skillDto, result);
    }

    @Test
    void testGetUserSkills() {
        Long userId = 1L;
        List<Skill> skills = List.of(new Skill());
        List<SkillDto> skillDtos = List.of(new SkillDto());

        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);
        when(skillMapper.toDto(skills)).thenReturn(skillDtos);

        List<SkillDto> result = skillService.getUserSkills(userId);

        assertEquals(skillDtos, result);
        verify(skillRepository).findAllByUserId(userId);
        verify(skillMapper).toDto(skills);
    }

    @Test
    void testGetOfferedSkills() {
        Long userId = 1L;
        List<Skill> offeredSkills = List.of(new Skill());
        List<SkillDto> offeredSkillDtos = List.of(new SkillDto());

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(offeredSkills);
        when(skillMapper.toDto(offeredSkills)).thenReturn(offeredSkillDtos);

        List<SkillDto> result = skillService.getOfferedSkills(userId);

        assertEquals(offeredSkillDtos, result);
        verify(skillRepository).findSkillsOfferedToUser(userId);
        verify(skillMapper).toDto(offeredSkills);
    }

    @Test
    void testGetSkillByIdOrThrow() {
        Long skillId = 1L;
        Skill skill = new Skill();

        when(skillRepository.getById(skillId)).thenReturn(skill);

        Skill result = skillService.getSkillByIdOrThrow(skillId);

        assertEquals(skill, result);
        verify(skillRepository).getById(skillId);
    }

    @Test
    void testGetSkillByIdOrThrowThrowsException() {
        Long skillId = 1L;

        when(skillRepository.getById(skillId)).thenReturn(null);

        SkillResourceNotFoundException exception = assertThrows(SkillResourceNotFoundException.class, () -> {
            skillService.getSkillByIdOrThrow(skillId);
        });

        assertEquals("Skill not found in DB with id = " + skillId, exception.getMessage());
        verify(skillRepository).getById(skillId);
    }

    @Test
    void testAcquireSkillFromOffers() {
        Long skillId = 1L;
        Long userId = 1L;
        User user = mock(User.class);
        List<Skill> skills = new ArrayList<>();
        Skill skill = new Skill();
        SkillDto skillDto = new SkillDto();

        skill.setId(skillId);
        skillDto.setId(skillId);
        when(skillRepository.getById(skillId)).thenReturn(skill);
        when(userService.findUserById(userId)).thenReturn(user);
        when(user.getSkills()).thenReturn(skills);
        when(user.getId()).thenReturn(userId);
        when(skillOfferService.getCountSkillOffersForUser(skillId, userId)).thenReturn(4);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertEquals(skillDto, result);
        verify(skillRepository).getById(skillId);
    }
}
