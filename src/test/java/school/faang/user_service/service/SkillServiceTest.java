package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Captor
    private ArgumentCaptor<Skill> captor;

    @Captor
    private ArgumentCaptor<Long> captor2;

    @Test
    public void testCreateExistingInDataBase() {
        SkillDto skillDto = prepareData(true);

        Throwable exception = assertThrows(
                DataValidationException.class,
                () ->  skillService.create(skillDto));

        assertEquals("such skill is already exist in database",
                exception.getMessage());
    }

    @Test
    public void testCreateNotExistingInDataBase() {
        SkillDto skillDto = prepareData(false);
        Skill skill = skillMapper.toEntity(skillDto);
        Mockito.when(skillRepository.save(skill)).thenReturn(skill);

        SkillDto returnedSkillDto = skillService.create(skillDto);

        Mockito.verify(skillRepository, Mockito.times(1))
               .save(captor.capture());
        var returnedSkill = captor.getValue();

        assertEquals(skill.getTitle(), returnedSkill.getTitle());
        assertEquals(skillDto.getTitle(), returnedSkillDto.getTitle());
    }

    @Test
    public void testGetUserSkills() {
        long userId = 1;
        skillService.getUserSkills(userId);

        Mockito.verify(skillRepository, Mockito.times(1))
                .findAllByUserId(captor2.capture());
        var argument = captor2.getValue();

        assertEquals(userId, argument);
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1;
        skillService.getOfferedSkills(userId);

        Mockito.verify(skillRepository, Mockito.times(1))
                .findSkillsOfferedToUser(captor2.capture());
        var argument = captor2.getValue();

        assertEquals(userId, argument);
    }

    @Test
    public void testAcquireExistingSkill() {
        long skillId = 1;
        long userId = 1;
        Skill skill = new Skill();
        Mockito.when(skillRepository.findUserSkill(skillId, userId))
                .thenReturn(Optional.of(skill));

        Throwable exception = assertThrows(
                DataValidationException.class,
                () ->  skillService.acquireSkillFromOffers(skillId, userId));

        assertEquals("this user already got such skill",
                exception.getMessage());
    }

    @Test
    public void testAcquireSkillOfferedMoreThanThreeTimes() {
        long skillId = 1;
        long userId = 1;
        Mockito.when(skillRepository.findUserSkill(skillId, userId))
                .thenReturn(Optional.empty());

        Skill skill = new Skill();
        skill.setGuarantees(new ArrayList<>());

        Recommendation recommendation = new Recommendation();
        User author = new User();
        recommendation.setAuthor(author);
        recommendation.setReceiver(new User());

        SkillOffer skillOffer1 = new SkillOffer();
        skillOffer1.setSkill(skill);
        skillOffer1.setRecommendation(recommendation);

        SkillOffer skillOffer2 = new SkillOffer();
        skillOffer2.setSkill(skill);
        skillOffer2.setRecommendation(recommendation);

        SkillOffer skillOffer3 = new SkillOffer();
        skillOffer3.setSkill(skill);
        skillOffer3.setRecommendation(recommendation);

        List<SkillOffer> skillOffers =
                Arrays.asList(skillOffer1, skillOffer2, skillOffer3);

        Mockito.when(skillOfferRepository.findAllOffersOfSkill(skillId, userId))
                .thenReturn(skillOffers);

        SkillDto returnedSkillDto =
                skillService.acquireSkillFromOffers(skillId, userId);

        Mockito.verify(skillRepository, Mockito.times(1))
                .assignSkillToUser(skillId, userId);
        Mockito.verify(skillMapper, Mockito.times(1))
                .toDto(captor.capture());
        var argument = captor.getValue();

        List<UserSkillGuarantee> returnedGuarantees = argument.getGuarantees();
        User returnedAuthor = returnedGuarantees
                .get(returnedGuarantees.size() - 1).getGuarantor();
        assertEquals(author, returnedAuthor);

        assertEquals(skill.getId(), returnedSkillDto.getId());
    }

    @Test
    public void testAcquireSkillOfferedLessThanThreeTimes() {
        long skillId = 1;
        long userId = 1;
        Mockito.when(skillRepository.findUserSkill(skillId, userId))
                .thenReturn(Optional.empty());
        Mockito.when(
                skillOfferRepository.findAllOffersOfSkill(skillId, userId))
                .thenReturn(new ArrayList<SkillOffer>());

        SkillDto skillDto =
                skillService.acquireSkillFromOffers(skillId, userId);
        assertNull(skillDto);
    }

    private SkillDto prepareData(boolean existingByTitle) {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("any");
        Mockito.when(skillRepository.existsByTitle(
                skillMapper.toEntity(skillDto).getTitle()))
                .thenReturn(existingByTitle);

        return skillDto;
    }

}
