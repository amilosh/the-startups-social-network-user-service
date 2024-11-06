package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillAcquireDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.skill.SkillDuplicateException;
import school.faang.user_service.exception.skill.SkillResourceNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validation.skill.SkillValidation;

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

    public SkillDto acquireSkillFromOffers(SkillAcquireDto skillAcquireDto) {
        Skill skill = getSkillByIdOrThrow(skillAcquireDto.getSkillId());
        User user = userService.findUser(skillAcquireDto.getUserId());

        if(user.getSkills().contains(skill)) {
            throw new SkillDuplicateException("User " + user.getUsername() + " already possesses the skill " + skill.getTitle());
        }

        int skillOfferCount = skillOfferService.getCountSkillOffersForUser(skill.getId(), user.getId());

        if(skillOfferCount >= OFFERS_AMOUNT) {
            skillRepository.assignSkillToUser(skill.getId(), user.getId());
        }

        return skillMapper.toDto(skill);
    }

    public Skill getSkillByIdOrThrow(Long id) {
        return Optional.ofNullable(skillRepository.getById(id))
            .orElseThrow(() -> new SkillResourceNotFoundException("Skill not found in DB with id = " + id));
    }
}
