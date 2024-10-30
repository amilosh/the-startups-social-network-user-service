package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.web.dto.mapper.SkillMapper;
import school.faang.user_service.web.dto.skill.SkillDto;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Override
    public SkillDto create(SkillDto skillDto) {
        return Optional.ofNullable(skillDto)
                .map(skillMapper::toEntity)
                .map(skillRepository::saveAndFlush)
                .map(skillMapper::toDto)
                .orElseThrow(() -> new IllegalStateException("Error save product"));
    }
}

