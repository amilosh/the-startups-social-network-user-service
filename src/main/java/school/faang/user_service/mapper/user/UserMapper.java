package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.promotion.UserResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.UserPromotion;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toDto(User user);

    List<UserDto> toListDto(List<User> users);

    List<User> toListUser(List<UserDto> users);

    @Mapping(source = "promotions", target = "promotionTariff", qualifiedByName = "mapTariff")
    @Mapping(source = "promotions", target = "numberOfViews", qualifiedByName = "mapNumberOfViews")
    UserResponseDto toUserResponseDto(User user);

    @Named("mapTariff")
    default String mapTariff(List<UserPromotion> userPromotions) {
        Optional<UserPromotion> promotionOpt = getActivePromotion(userPromotions);
        return promotionOpt
                .map(userPromotion -> userPromotion.getPromotionTariff().toString())
                .orElse(null);
    }

    @Named("mapNumberOfViews")
    default Integer mapNumberOfViews(List<UserPromotion> userPromotions) {
        Optional<UserPromotion> promotionOpt = getActivePromotion(userPromotions);
        return promotionOpt
                .map(UserPromotion::getNumberOfViews)
                .orElse(null);
    }

    private Optional<UserPromotion> getActivePromotion(List<UserPromotion> userPromotions) {
        return userPromotions
                .stream()
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .findFirst();
    }
}
