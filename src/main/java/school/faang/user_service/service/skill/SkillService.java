package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.skill.SkillValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillService {

    private static final int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final UserValidator userValidator;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;
    private final SkillValidator skillValidator;
    private final SkillCandidateMapper skillCandidateMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserService userService;

    public SkillDto create(SkillDto skillDto) {
        skillValidator.validateTitle(skillDto);

        Skill skill = skillMapper.toEntity(skillDto);
        skill = skillRepository.save(skill);
        log.info("Навык {} был записан в БД", skill.getTitle());
        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        userValidator.validateUserExistence(userService.existsById(userId));

        List<Skill> skills = skillRepository.findAllByUserId(userId);
        log.info("У пользователя {} было найдено {} навыков", userId, skills.size());
        return skillMapper.toDtoList(skills);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        userValidator.validateUserExistence(userService.existsById(userId));

        List<SkillDto> skills = skillMapper.toDtoList(skillRepository.findSkillsOfferedToUser(userId));

        log.info("У пользователя {} было найдено {} предложенных навыков", userId, skills.size());
        return skillCandidateMapper.toSkillCandidateDtoList(skills);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        userValidator.validateUserExistence(userService.existsById(userId));
        Skill skill = skillValidator.skillAlreadyExists(skillId);

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
}