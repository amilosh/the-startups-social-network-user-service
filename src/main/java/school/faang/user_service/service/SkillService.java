package school.faang.user_service.service;
import lombok.RequiredArgsConstructor;
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
import school.faang.user_service.repository.UserSkillGuaranteeRepository; // Ensure this repository is created
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository; // Injected for saving guarantors

    private static final int MIN_SKILL_OFFERS = 3;

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {

        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            return null;
        }

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (skillOffers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Not enough offers to acquire the skill.");
        }


        skillRepository.assignSkillToUser(skillId, userId);
//
//        skillOffers.forEach(skillOffer -> {
//            UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
//                    .user(new User())
//                    .skill(new Skill())
//                    .guarantor(new User())
//                    .build();
//            userSkillGuaranteeRepository.save(guarantee);
//        });


        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill not found."));
        return skillMapper.toDto(skill);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        Map<Skill, Long> skillCountMap = offeredSkills.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));
        return skillCountMap.entrySet().stream()
                .map(entry -> new SkillCandidateDto(skillMapper.toDto(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with this title already exists");
        }

        Skill skill = skillMapper.toEntity(skillDto);
        skill = skillRepository.save(skill);
        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }
}