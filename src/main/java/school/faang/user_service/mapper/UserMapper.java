package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SubscriptionUserDto subscriptionUserDto);

    SubscriptionUserDto toDto(User user);
}
