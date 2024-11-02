package school.faang.user_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.contact.Contact;

@Builder
public record UserFilterDto (
    @NotNull @NotEmpty String namePattern,
    @NotNull @NotEmpty String aboutPattern,
    @NotNull @NotEmpty String emailPattern,
    @NotNull @NotEmpty Contact contactPattern,
    @NotNull @NotEmpty String countryPattern,
    @NotNull @NotEmpty String cityPattern,
    @NotNull @NotEmpty String phonePattern,
    @NotNull @NotEmpty Skill skillPattern,
    @NotNull @Positive Integer experienceMin,
    @NotNull @Positive Integer experienceMax,
    @NotNull @Positive Integer page,
    @NotNull @Positive Integer pageSize
) {
}
