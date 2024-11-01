package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

@RequiredArgsConstructor
@Component
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    public SkillDto create(SkillDto skillDto) {
        Skill skill = skillMapper.dtoToEntity(skillDto);
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Такой навык уже есть");
        }

        skillRepository.save(skill);
        return skillMapper.toDto(skill);
    }
}
