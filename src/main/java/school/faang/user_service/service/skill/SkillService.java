package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillAcquireDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.skill.SkillDuplicateException;
import school.faang.user_service.exception.skill.SkillResourceNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillOfferService;
import school.faang.user_service.service.UserSkillGuaranteeService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.skill.SkillValidation;
import school.faang.user_service.validation.skill.SkillValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {
    public static final int OFFERS_AMOUNT = 3;

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillValidation skillValidation;
    private final UserService userService;
    private final SkillOfferService skillOfferService;
    private final SkillValidator skillValidator;
    private final UserSkillGuaranteeService userSkillGuaranteeService;

    public SkillDto create(SkillDto skillDto) {
        Skill skill = skillMapper.toEntity(skillDto);

        skillValidation.validateDuplicate(skill);
        skill = skillRepository.save(skill);

        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skillMapper.toDto(skills);
    }

    public SkillCandidateDto getOfferedSkills(SkillCandidateDto skillCandidateDto) {
        Skill skill = skillMapper.toEntity(skillCandidateDto.getSkill());
        Long offersAmount = skillOfferService.getCountSkillOffersBySkill(skill.getId());

        skillCandidateDto.setOffersAmount(offersAmount);

        return skillCandidateDto;
    }

    public List<SkillDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        return skillMapper.toDto(offeredSkills);
    }

    public SkillDto acquireSkillFromOffers(SkillAcquireDto skillAcquireDto) {
        Skill skill = getSkillByIdOrThrow(skillAcquireDto.getSkillId());
        User user = userService.findUser(skillAcquireDto.getUserId());

        if (user.getSkills().contains(skill)) {
            throw new SkillDuplicateException("User " + user.getUsername() + " already possesses the skill " + skill.getTitle());
        }

        int skillOfferCount = skillOfferService.getCountSkillOffersForUser(skill.getId(), user.getId());

        if (skillOfferCount >= OFFERS_AMOUNT) {
            skillRepository.assignSkillToUser(skill.getId(), user.getId());
        }

        return skillMapper.toDto(skill);
    }

    public Skill getSkillByIdOrThrow(Long id) {
        return Optional.ofNullable(skillRepository.getById(id))
            .orElseThrow(() -> new SkillResourceNotFoundException("Skill not found in DB with id = " + id));
    }

    public List<Long> getSkillGuaranteeIds(Skill skill) {
        skillValidator.validateSkillExists(skill.getId());
        return skill.getGuarantees().stream()
            .map(userSkillGuarantee -> userSkillGuarantee.getGuarantor().getId())
            .toList();
    }

    @Transactional
    public void addGuarantee(Recommendation recommendation) {
        List<Skill> userSkills = userService.findUser(recommendation.getReceiver().getId()).getSkills();
        List<Long> recommendedSkillsIds = recommendation.getSkillOffers().stream().map(SkillOffer::getId).toList();

        userSkills.stream()
            .filter(skill -> filterSkillsForGuarantee(skill, recommendedSkillsIds, recommendation))
            .forEach(skill -> {
                userSkillGuaranteeService.addSkillGuarantee(skill, recommendation);
                skillRepository.save(skill);
            });
    }

    private boolean filterSkillsForGuarantee(Skill skill, List<Long> recommendedSkillsIds, Recommendation
        recommendation) {
        return recommendedSkillsIds.contains(skill.getId()) &&
            !getSkillGuaranteeIds(skill).contains(recommendation.getAuthor().getId());
    }

    /**
     * Check if a skill with the given id exists in the database.
     *
     * @param skillId the id of the skill to check
     * @return true if the skill exists, false otherwise
     */
    public boolean checkIfSkillExistsById(Long skillId) {
        return skillRepository.existsById(skillId);
    }

    /**
     * Retrieve a skill by its ID.
     *
     * @param skillId the id of the skill to retrieve
     * @return the skill associated with the given id
     */
    public Skill getSkillById(Long skillId) {
        return skillRepository.getReferenceById(skillId);
    }
}
