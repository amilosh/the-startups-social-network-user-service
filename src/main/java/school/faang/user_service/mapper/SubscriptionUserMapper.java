package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubscriptionUserMapper {

    User toEntity(SubscriptionUserDto subscriptionUserDto);

    SubscriptionUserDto toDto(User user);
}
