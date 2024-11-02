package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.service.validation.SkillOfferValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSkillGuaranteeServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private SkillOfferValidation skillOfferValidation;
    @InjectMocks
    private UserSkillGuaranteeService userSkillGuaranteeService;

    @Test
    void testCreateGuaranteesWithNullGuarantor() {
        assertThrows(NullPointerException.class, () -> userSkillGuaranteeService.createGuarantees(List.of(), null,
                new User()));
    }

    @Test
    void testCreateGuaranteesWithNullReceiver() {
        assertThrows(NullPointerException.class, () -> userSkillGuaranteeService.createGuarantees(List.of(), new User(),
                null));
    }

    @Test
    void testCreateGuaranteesValidation() {
        List<SkillOfferDto> skillOffers = List.of(
                SkillOfferDto.builder().skillId(1L).build()
        );
        User guarantor = User.builder().id(1L).build();
        User receiver = User.builder().id(2L)
                .skills(List.of())
                .build();
        skillOffers.forEach(skillOffer -> when(skillRepository.findById(skillOffer.getSkillId()))
                .thenReturn(Optional.of(Skill.builder().id(skillOffer.getSkillId()).build())));

        userSkillGuaranteeService.createGuarantees(skillOffers, guarantor, receiver);

        verify(skillOfferValidation).validate(skillOffers.get(0));
    }

    @Test
    void testCreateGuaranteesCreated() {
        List<SkillOfferDto> skillOffers = List.of(
                SkillOfferDto.builder().skillId(1L).build(),
                SkillOfferDto.builder().skillId(2L).build()
        );
        User guarantor = User.builder().id(1L).build();
        User receiver = User.builder().id(2L)
                .skills(List.of(Skill.builder().id(1L).guarantees(new ArrayList<>()).build()))
                .build();
        skillOffers.forEach(skillOffer -> when(skillRepository.findById(skillOffer.getSkillId()))
                .thenReturn(Optional.of(Skill.builder().id(skillOffer.getSkillId()).build())));
        when(userSkillGuaranteeRepository.findByUserAndGuarantorAndSkill(any(), any(), any()))
                .thenReturn(Optional.empty());
        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                .user(receiver)
                .guarantor(guarantor)
                .skill(receiver.getSkills().get(0))
                .build();

        userSkillGuaranteeService.createGuarantees(skillOffers, guarantor, receiver);

        verify(userSkillGuaranteeRepository, times(1)).save(any());
        verify(userSkillGuaranteeRepository, times(1)).save(userSkillGuarantee);
    }
}