package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.UserResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.pojo.person.ContactInfo;
import school.faang.user_service.pojo.person.Person;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toDto(User user);

    List<UserDto> toListDto(List<User> users);

    List<User> toListUser(List<UserDto> users);

    @Mapping(target = "username", expression = "java(person.getFirstName() + ' ' + person.getLastName())")
    @Mapping(target = "aboutMe", source = ".", qualifiedByName = "about")
    @Mapping(target = "country", ignore = true)
    User toUser(Person person);

    @Mapping(source = "promotion", target = "promotionTariff", qualifiedByName = "mapTariff")
    @Mapping(source = "promotion", target = "numberOfViews", qualifiedByName = "mapNumberOfViews")
    UserResponseDto toUserResponseDto(User user);

    @Named("mapTariff")
    default String mapTariff(UserPromotion userPromotion) {
        return Optional.ofNullable(userPromotion)
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .map(promotion -> promotion.getPromotionTariff().toString())
                .orElse(null);
    }

    @Named("mapNumberOfViews")
    default Integer mapNumberOfViews(UserPromotion userPromotion) {
        return Optional.ofNullable(userPromotion)
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .map(UserPromotion::getNumberOfViews)
                .orElse(null);
    }

    default Optional<UserPromotion> getActivePromotion(UserPromotion userPromotion) {
        return Optional.ofNullable(userPromotion)
                .filter(promotion -> promotion.getNumberOfViews() > 0);
    }
    @Named("about")
    default String about(Person person) {
        String state = person.getContactInfo().getAddress().getState();
        String faculty = person.getEducation().getFaculty();
        Integer yearOfStudy = person.getEducation().getYearOfStudy();
        String major = person.getEducation().getMajor();
        String employer = person.getEmployer();
        StringBuilder builder = new StringBuilder();
        if (state != null) {
            builder.append("state: ").append(state).append(", ");
        }
        builder.append("faculty: ").append(faculty).append(", ");
        builder.append("yearOfStudy: ").append(yearOfStudy).append(", ");
        builder.append("major: ").append(major).append(", ");
        if (employer != null) {
            builder.append("employer: ").append(employer).append(".");
        } else {
            builder.append(".");
        }
        return builder.toString();
    }
}
