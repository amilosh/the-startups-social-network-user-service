package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class SkillCandidateDto {
    private SkillDto skillDto;
    private long offersAmount;

    public SkillCandidateDto(SkillDto skillDto) {
        this.skillDto = skillDto;
    }
}
