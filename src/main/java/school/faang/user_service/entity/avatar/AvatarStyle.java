package school.faang.user_service.entity.avatar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AvatarStyle {
    AVATAAARS("avataaars"),
    BOTTTTS("bottts"),
    PIXEL_ART("pixel-art");

    private final String styleName;
}