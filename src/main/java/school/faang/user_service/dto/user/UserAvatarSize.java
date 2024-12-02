package school.faang.user_service.dto.user;

import lombok.Getter;

@Getter
public enum UserAvatarSize {
    LARGE(1080),
    SMALL(170);

    private final int maxSideSize;

    UserAvatarSize(int maxSideSize) {
        this.maxSideSize = maxSideSize;
    }
}
