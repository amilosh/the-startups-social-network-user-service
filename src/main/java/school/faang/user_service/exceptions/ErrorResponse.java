package school.faang.user_service.exceptions;

import lombok.Builder;

@Builder
public record ErrorResponse(String errorMessage) {
}
