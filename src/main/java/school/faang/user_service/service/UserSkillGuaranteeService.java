package school.faang.user_service.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.service.validation.SkillOfferValidation;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSkillGuaranteeService {
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillOfferValidation skillOfferValidation;

    public void createGuarantees(List<SkillOfferDto> skillOffers, @NonNull User guarantor, @NonNull User receiver) {
        skillOffers.forEach(skillOffer -> {
            skillOfferValidation.validate(skillOffer);
            Skill skill = skillRepository.findById(skillOffer.getSkillId())
                    .orElseThrow(() -> new DataValidationException("Skill not found"));
            List<Skill> receiverSkills = receiver.getSkills();
            if (receiverSkills.contains(skill)
                    && userSkillGuaranteeRepository
                    .findByUserAndGuarantorAndSkill(receiver, guarantor, skill).isEmpty()) {
                UserSkillGuarantee guarantee = createGuarantee(receiver, guarantor, skill);
                skill.getGuarantees().add(guarantee);
            }
        });
    }

    private UserSkillGuarantee createGuarantee(User receiver, User guarantor, Skill skill) {
        UserSkillGuarantee guarantee = new UserSkillGuarantee();
        guarantee.setUser(receiver);
        guarantee.setGuarantor(guarantor);
        guarantee.setSkill(skill);
        return userSkillGuaranteeRepository.save(guarantee);
    }
}
