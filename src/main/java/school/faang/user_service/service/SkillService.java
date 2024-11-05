package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillValidation skillValidation;
    private final UserService userService;
    private final SkillOfferService skillOfferService;
    public final int OFFERS_AMOUNT = 3;

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
        int offersAmount = skillOfferService.getCountSkillOffersBySkill(skill.getId());

        skillCandidateDto.setOffersAmount(Long.valueOf(offersAmount));

        return skillCandidateDto;
    }

    public SkillDto acquireSkillFromOffers(SkillAcquireDto skillAcquireDto) {
        Skill skill = getByIdOrThrow(skillAcquireDto.getSkillId());
        User user = userService.getByIdOrThrow(skillAcquireDto.getUserId());

        if(user.getSkills().contains(skill)) {
            throw new SkillDuplicateException("Пользователь " + user.getUsername() + " уже владеет навыком " + skill.getTitle());
        }

        int skillOfferCount = skillOfferService.getCountSkillOffersForUser(skill.getId(), user.getId());

        if(skillOfferCount >= OFFERS_AMOUNT) {
            skillRepository.assignSkillToUser(skill.getId(), user.getId());
        }

        return skillMapper.toDto(skill);
    }

    public Skill getByIdOrThrow(Long id) {
        Skill skill = skillRepository.getById(id);

        if(skill == null) {
            throw new SkillResourceNotFoundException("Не существует навыка в БД по id = " + id);
        }

        return skill;
    }
}
