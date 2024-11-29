package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record EventFilterDto(String title, Long ownerId, String location) {

}
