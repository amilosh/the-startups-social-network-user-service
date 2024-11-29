package school.faang.user_service.dto.user_jira;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class UserJiraCreateUpdateDto {

    @NotBlank(message = "Jira user email cannot be null or empty!")
    @Length(min = 1, max = 64, message = "Jira user email length cannot exceed 64 symbols!")
    private String jiraEmail;

    @NotBlank(message = "User Jira account ID cannot be null or empty!")
    @Length(min = 1, max = 128, message = "User Jira account ID length cannot exceed 128 symbols!")
    private String jiraAccountId;

    @NotBlank(message = "User Jira token cannot be null or empty!")
    @Length(min = 1, max = 256, message = "User Jira token length cannot exceed 128 symbols!")
    private String jiraToken;
}
