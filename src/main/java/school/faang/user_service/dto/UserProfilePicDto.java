package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record UserProfilePicDto(
        String fileId,
        String smallFileId
) {
}