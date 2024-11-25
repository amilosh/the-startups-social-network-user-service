package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillValidator skillValidator;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;

    public SkillDto create(SkillDto skillDto) {
        Skill skill = skillMapper.toEntity(skillDto);

        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Skill " + skill.getId() + " already exists");
        } else {
            skillRepository.save(skill);
            log.info("Skill {} successfully created", skill.getId());
        }
        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        log.info("Getting user {} skills", userId);

        return skillRepository.findAllByUserId(userId).stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        log.info("Getting user {} offered skills", userId);

        return skillRepository.findSkillsOfferedToUser(userId).stream()
               .map(skillCandidateMapper::toDto)
               .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> skill = skillRepository.findUserSkill(skillId, userId);

        if (skill.isEmpty()) {
            log.info("Getting the acquired skill {} from offers by user {}", skillId, userId);
            List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            skillValidator.validateSkillByMinSkillOffer(offers.size(), skillId, userId);
            skillRepository.assignSkillToUser(skillId, userId);
            addUserSkillGuarantee(offers);

            return skillMapper.toDto(offers.get(0).getSkill());
        } else {
            throw new DataValidationException("User " + userId + " already has skill with id " + skillId);
        }
    }

    private void addUserSkillGuarantee(List<SkillOffer> offers) {
        log.info("Adding a skill guarantee");

        List<UserSkillGuarantee> guarantees = offers.stream()
                .map(offer -> UserSkillGuarantee.builder()
                        .user(offer.getRecommendation().getReceiver())
                        .skill(offer.getSkill())
                        .guarantor(offer.getRecommendation().getAuthor())
                        .build())
                .distinct()
                .toList();
        userSkillGuaranteeRepository.saveAll(guarantees);
    }
}
