package school.faang.user_service.dto;

import school.faang.user_service.entity.User;


public record EventFilterDto(String title, User owner, String location) {

}
