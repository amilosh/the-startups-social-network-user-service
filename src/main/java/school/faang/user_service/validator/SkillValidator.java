package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private final static int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;

    public void validateSkill(SkillDto skillDto) {
        if (skillDto.getTitle() == null) {
            throw new DataValidationException("Скилл должен иметь название");
        }

        if (skillDto.getTitle().isBlank()) {
            throw new DataValidationException("Имя скилла не может быть пустым");
        }
    }

    public void validateExistTitle(String title) {
        if (skillRepository.existsByTitle(title)) {
            throw new DataValidationException(title);
        }
    }

    public void validateUserSkillExist(long skillId, long userId) {
        if (!skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("У пользователя уже есть этот скилл");
        }
    }

    public void validateSkillOfferCount(long skillId, long userId) {
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Недостаточно предложений для получения скилла");
        }
    }
}
