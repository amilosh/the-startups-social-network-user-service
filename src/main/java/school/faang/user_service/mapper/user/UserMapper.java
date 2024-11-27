package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.UserResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.pojo.person.Address;
import school.faang.user_service.pojo.person.ContactInfo;
import school.faang.user_service.pojo.person.Education;
import school.faang.user_service.pojo.person.PersonFromFile;
import school.faang.user_service.pojo.person.PersonFlat;
import school.faang.user_service.pojo.person.PreviousEducation;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toDto(User user);

    List<UserDto> toListDto(List<User> users);

    List<User> toListUser(List<UserDto> users);

    @Mapping(target = "username", expression = "java(personFromFile.getFirstName() + ' ' + personFromFile.getLastName())")
    @Mapping(target = "aboutMe", source = ".", qualifiedByName = "about")
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "email", expression = "java(personFromFile.getContactInfo().getEmail())")
    @Mapping(target = "phone", expression = "java(personFromFile.getContactInfo().getPhone())")
    @Mapping(target = "city", expression = "java(personFromFile.getContactInfo().getAddress().getCity())")
    User toUser(PersonFromFile personFromFile);

    @Mapping(source = "promotion", target = "promotionTariff", qualifiedByName = "mapTariff")
    @Mapping(source = "promotion", target = "numberOfViews", qualifiedByName = "mapNumberOfViews")
    UserResponseDto toUserResponseDto(User user);

    @Mapping(target = "contactInfo", source = ".", qualifiedByName = "toContactInfo" )
    @Mapping(target = "education", source = ".", qualifiedByName = "toEducation" )
    @Mapping(target = "previousEducation", source = ".", qualifiedByName = "toPreviousEducation" )
    PersonFromFile convertFlatToNested(PersonFlat personFlat);

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
    default String about(PersonFromFile personFromFile) {
        String state = personFromFile.getContactInfo().getAddress().getState();
        String faculty = personFromFile.getEducation().getFaculty();
        Integer yearOfStudy = personFromFile.getEducation().getYearOfStudy();
        String major = personFromFile.getEducation().getMajor();
        String employer = personFromFile.getEmployer();
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

    @Named("toContactInfo")
    default ContactInfo toContactInfo(PersonFlat personFlat) {
        ContactInfo contactInfo = new ContactInfo();
        Address address = new Address();
        address.setCity(personFlat.getCity());
        address.setCountry(personFlat.getCountry());
        address.setState(personFlat.getState());
        address.setStreet(personFlat.getStreet());
        address.setPostalCode(personFlat.getPostalCode());
        contactInfo.setAddress(address);
        contactInfo.setPhone(personFlat.getPhone());
        contactInfo.setEmail(personFlat.getEmail());
        return contactInfo;
    }

    @Named("toEducation")
    default Education toEducation(PersonFlat personFlat) {
        Education education = new Education();
        education.setFaculty(personFlat.getFaculty());
        education.setYearOfStudy(personFlat.getYearOfStudy());
        education.setMajor(personFlat.getMajor());
        education.setGpa(personFlat.getGpa());
        return education;
    }

    @Named("toPreviousEducation")
    default List<PreviousEducation> toPreviousEducation(PersonFlat personFlat) {
        PreviousEducation previousEducation = new PreviousEducation();
        previousEducation.setCompletionYear(personFlat.getYearOfStudy());
        previousEducation.setDegree(personFlat.getDegree());
        previousEducation.setInstitution(personFlat.getInstitution());
        return List.of(previousEducation);
    }

}
