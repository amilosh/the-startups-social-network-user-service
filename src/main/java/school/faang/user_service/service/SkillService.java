package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SkillService {
    private final static int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;

    public SkillDto create(SkillDto skillDto) {
        Skill skill = skillMapper.dtoToEntity(skillDto);

        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Такой навык уже есть");
        }

        skillRepository.save(skill);
        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skills.stream()
                .map(skill -> skillMapper.toDto(skill))
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);

        return skills.stream()
                .collect(Collectors.groupingBy(skill -> skillMapper.toDto(skill), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new SkillCandidateDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        if (!skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("У пользователя уже есть этот скилл");
        }

        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (offers.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            Optional<Skill> skill = skillRepository.findUserSkill(skillId, userId);
            return skill.map(skill1 -> skillMapper.toDto(skill1))
                    .orElseThrow(() -> new DataValidationException("Скилл не найден"));
        } else {
            throw new DataValidationException("Недостаточно предложений для получения скилла");
        }
    }
}
