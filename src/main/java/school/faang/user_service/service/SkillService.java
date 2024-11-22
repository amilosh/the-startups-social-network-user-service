package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final int MIN_SKILL_OFFERS = 3;
    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private SkillMapper skillMapper;

    @Autowired
    private SkillOfferRepository skillOfferRepository;

    public SkillDto create(SkillDto skillDto) {
        //проверить, что в бд нет скилла с таким названием
        //если это так, то создать новую запись в бд
        Skill skill = skillMapper.toEntity(skillDto);

        if (!skillRepository.existsByTitle(skill.getTitle())) {
            skill = skillRepository.save(skill);
        } else {
            throw new DataValidationException(
                    "such skill is already exist in database");
        }

        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        List<SkillDto> skillDtos = skills
                .stream()
                .map(x -> skillMapper.toDto(x))
                .toList();

        return skillDtos;
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        List<SkillCandidateDto> skillCandidateDtos = skills
                .stream()
                .map(x -> skillMapper.toDto(x))
                .map(x -> new SkillCandidateDto(x))
                .toList();

        return skillCandidateDtos;
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> op = skillRepository.findUserSkill(skillId, userId);
        SkillDto skillDto = null;

        if (op.isEmpty()) {
            List<SkillOffer> skillOffers =
                    skillOfferRepository.findAllOffersOfSkill(skillId, userId);

            if (skillOffers.size() >= MIN_SKILL_OFFERS) {
                skillRepository.assignSkillToUser(skillId, userId);
                Skill skill = null;

                for (SkillOffer s : skillOffers) {
                    User receiver = s.getRecommendation().getReceiver();
                    skill = s.getSkill();
                    User autor = s.getRecommendation().getAuthor();

                    UserSkillGuarantee guarantee = new UserSkillGuarantee();
                    guarantee.setUser(receiver);
                    guarantee.setSkill(skill);
                    guarantee.setGuarantor(autor);

                    skill.getGuarantees().add(guarantee);
                }

                skillDto = skillMapper.toDto(skill);
            }
        }

        return skillDto;
    }

}
