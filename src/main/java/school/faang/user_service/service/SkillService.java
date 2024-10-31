package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {

    private final SkillRepository skillRepo;

    public Skill getSkillById(Long id) {
        return skillRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
    }

    public List<Skill> getSkillsFrom(List<SkillOfferDto> skillOffers) {
        return skillOffers.stream()
                .map(skillOfferDto -> getSkillById(skillOfferDto.skillId()))
                .toList();
    }
}
