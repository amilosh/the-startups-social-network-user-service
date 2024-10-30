package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Spy
    private SkillCandidateMapper skillCandidateMapper = Mappers.getMapper(SkillCandidateMapper.class);

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private SkillService skillService;

    private long userId;
    private long skillId;
    private User user;
    private SkillDto skillDto;
    private Skill firstSkill;
    private Skill secondSkill;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        user = new User();
        user.setId(userId);

        skillId = 1L;
        skillDto = new SkillDto(skillId, "New skill");

        firstSkill = new Skill();
        firstSkill.setId(1L);
        firstSkill.setTitle("First skill");

        secondSkill = new Skill();
        secondSkill.setId(2L);
        secondSkill.setTitle("Second skill");
    }

    @Test
    public void testCreateSkillWithEmptyTitle() {
        SkillDto skillDto = new SkillDto(1L, null);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreateSkillWithBlankTitle() {
        SkillDto skillDto = new SkillDto(1L, "");

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreateSkillWhichAlreadyExists() {
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreateSkill() {
        Skill skill = skillMapper.toEntity(skillDto);
        when(skillRepository.save(skill)).thenReturn(skill);

        SkillDto createSkillDto = skillService.create(skillDto);

        verify(skillRepository, times(1)).save(skill);
        assertEquals(skillDto.getId(), createSkillDto.getId());
        assertEquals(skillDto.getTitle(), createSkillDto.getTitle());
    }

    @Test
    public void testGetUserSkillsForANonExistsUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> skillService.getUserSkills(userId));
    }

    @Test
    public void testGetUserSkills() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(firstSkill, secondSkill));

        List<SkillDto> skills = skillService.getUserSkills(userId);

        verify(skillRepository, times(1)).findAllByUserId(userId);

        assertEquals(firstSkill.getId(), skills.get(0).getId());
        assertEquals(firstSkill.getTitle(), skills.get(0).getTitle());
        assertEquals(secondSkill.getId(), skills.get(1).getId());
        assertEquals(secondSkill.getTitle(), skills.get(1).getTitle());
    }

    @Test
    public void testGetOfferedSkillsForANonExistsUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> skillService.getOfferedSkills(userId));
    }

    @Test
    public void testGetOfferedSkills() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(firstSkill, secondSkill));

        List<SkillCandidateDto> skills = skillService.getOfferedSkills(userId);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        assertEquals(firstSkill.getTitle(), skills.get(0).getSkill().getTitle());
        assertEquals(1, skills.get(0).getOffersAmount());
        assertEquals(secondSkill.getTitle(), skills.get(1).getSkill().getTitle());
        assertEquals(1, skills.get(1).getOffersAmount());
    }

    @Test
    public void testAcquireSkillFromOffersForANonExistentUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void testAcquireSkillFromOffersForANonExistentSkill() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void acquireSkillFromOffersSkillHasAlreadyBeenAdded() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(firstSkill));

        SkillDto skillDto = skillService.acquireSkillFromOffers(skillId, userId);

        assertEquals(firstSkill.getId(), skillDto.getId());
        assertEquals(firstSkill.getTitle(), skillDto.getTitle());
        verify(skillOfferRepository, times(0)).findAllOffersOfSkill(skillId, userId);
    }

    @Test
    public void acquireSkillFromOffersSkillWhenLessThan3Guarantors() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId))
                .thenReturn(List.of(new SkillOffer(), new SkillOffer()));

        SkillDto skillDto = skillService.acquireSkillFromOffers(skillId, userId);

        assertEquals(firstSkill.getId(), skillDto.getId());
        assertEquals(firstSkill.getTitle(), skillDto.getTitle());
        verify(skillRepository, times(0)).assignSkillToUser(skillId, userId);
    }

    @Test
    public void acquireSkillFromOffersSkill() {
        Recommendation recommendation = Recommendation.builder()
                .author(new User())
                .receiver(new User())
                .build();
        SkillOffer firstSkillOffer = SkillOffer.builder()
                .recommendation(recommendation)
                .skill(firstSkill)
                .build();
        SkillOffer secondSkillOffer = SkillOffer.builder()
                .recommendation(recommendation)
                .skill(firstSkill)
                .build();
        SkillOffer thirdSkillOffer = SkillOffer.builder()
                .recommendation(recommendation)
                .skill(firstSkill)
                .build();
        List<SkillOffer> skillOffers = List.of(firstSkillOffer, secondSkillOffer, thirdSkillOffer);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);

        SkillDto skillDto = skillService.acquireSkillFromOffers(skillId, userId);

        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
        verify(userSkillGuaranteeRepository, times(skillOffers.size())).save(any());
        assertEquals(firstSkill.getId(), skillDto.getId());
        assertEquals(firstSkill.getTitle(), skillDto.getTitle());
    }
}