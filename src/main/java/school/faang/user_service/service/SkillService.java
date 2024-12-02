package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class SkillService {
    private final static int MIN_SKILL_OFFERS = 3;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private SkillMapper skillMapper;

    @Autowired
    private SkillOfferRepository skillOfferRepository;

    public SkillDto create(SkillDto skillDto) {
        Skill skill = skillMapper.toEntity(skillDto);

        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException(
                    "such skill is already exist in database");
        } else {
            skill = skillRepository.save(skill);
        }

        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills
                .stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skills
                .stream()
                .map(skillMapper::toDto)
                .map(x -> {
                         SkillCandidateDto candidate = new SkillCandidateDto();
                         candidate.setSkillDto(x);
                         return candidate;
                     })
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> optionalSkill =
                skillRepository.findUserSkill(skillId, userId);
        if (optionalSkill.isPresent()) {
            //return null;
            throw new DataValidationException(
                    "this user already got such skill");
        }

        SkillDto skillDto = null;
        List<SkillOffer> skillOffers =
                skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (skillOffers.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            Skill skill = null;

            for (SkillOffer skillOffer : skillOffers) {
                User receiver = skillOffer.getRecommendation().getReceiver();
                skill = skillOffer.getSkill();
                User autor = skillOffer.getRecommendation().getAuthor();

                UserSkillGuarantee guarantee = new UserSkillGuarantee();
                guarantee.setUser(receiver);
                guarantee.setSkill(skill);
                guarantee.setGuarantor(autor);

                skill.getGuarantees()
                        .add(guarantee);
            }

            skillDto = skillMapper.toDto(skill);
        }

        return skillDto;
    }

}
