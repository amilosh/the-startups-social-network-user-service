package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.pojo.Person;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonToUserMapper {
    @Mapping(target = "username",  expression = "java(person.getFirstName() + person.getLastName())")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country.title", source = "contactInfo.address.country")
    @Mapping(target = "aboutMe", expression = "java(createAboutMe(person))")
    User personToUser(Person person);

    default String createAboutMe(Person person) {
        StringBuilder aboutMe = new StringBuilder();
        if (person.getContactInfo().getAddress().getState() != null
                && !person.getContactInfo().getAddress().getState().isEmpty()) {
            aboutMe.append(person.getContactInfo().getAddress().getState()).append(" ");
        }
        aboutMe.append(person.getEducation().getFaculty()).append(" ")
                .append(person.getEducation().getYearOfStudy()).append(" ")
                .append(person.getEducation().getMajor());
        if (person.getEmployer() != null && !person.getEmployer().isEmpty()) {
            aboutMe.append(" at ").append(person.getEmployer());
        }
        return aboutMe.toString();
    }
}
