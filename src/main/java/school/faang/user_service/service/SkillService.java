package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillService {

    private static final int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);

        Skill skill = skillMapper.toEntity(skillDto);
        skill = skillRepository.save(skill);
        log.info("Навык {} был записан в БД", skill.getTitle());
        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        validateUser(userId);

        List<Skill> skills = skillRepository.findAllByUserId(userId);
        log.info("У пользователя {} было найдено {} навыков", userId, skills.size());
        return skillMapper.toDtoList(skills);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        validateUser(userId);

        List<SkillDto> skills = skillMapper.toDtoList(skillRepository.findSkillsOfferedToUser(userId));

        log.info("У пользователя {} было найдено {} предложенных навыков", userId, skills.size());
        return skillCandidateMapper.toSkillCandidateDtoList(skills);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        validateUser(userId);
        Skill skill = validateSkill(skillId);

        Optional<Skill> skillOptional = skillRepository.findUserSkill(skillId, userId);
        if (skillOptional.isPresent()) {
            log.info("У пользователя {} уже есть навык {}", userId, skillId);
            return skillMapper.toDto(skillOptional.get());
        }

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            log.info("Пользователю {} добавлен навык {}", userId, skillId);

            for (SkillOffer skillOffer : skillOffers) {
                UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                        .user(skillOffer.getRecommendation().getReceiver())
                        .guarantor(skillOffer.getRecommendation().getAuthor())
                        .skill(skillOffer.getSkill())
                        .build();
                userSkillGuaranteeRepository.save(guarantee);
            }
        }
        return skillMapper.toDto(skill);
    }

    private Skill validateSkill(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Такого навыка в БД не существует"));
    }

    private void validateUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("Такого пользователя в БД не существует"));
    }

    private void validateSkill(SkillDto skillDto) {
        if (skillDto.getTitle() == null || skillDto.getTitle().isBlank()) {
            throw new DataValidationException("Название навыка не может быть пустым");
        }

        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Навык с таким названием уже существует");
        }
    }
}