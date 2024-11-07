package school.faang.user_service.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFilterDto {
    private Long id;
    private String namePattern;
    private String phone;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private Integer experience;
}
