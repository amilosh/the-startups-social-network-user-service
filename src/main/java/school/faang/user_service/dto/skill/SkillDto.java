package school.faang.user_service.dto.skill;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import lombok.Data;

@Data
public class SkillDto {
    @NotNull
    private Long id;
    @NotNull
    private String title;
    @Nullable
    private List<Long> userIds;
    @Nullable
    private List<Long> guaranteeIds;

    public SkillDto(@NotNull Long id, @NotNull String title, @Nullable List<Long> userIds, @Nullable List<Long> guaranteeIds) {
        this.id = Objects.requireNonNull(id, "Skill id is null");
        this.title = Objects.requireNonNull(title, "Skill title is null");
        this.userIds = userIds;
        this.guaranteeIds = guaranteeIds;
    }
}
