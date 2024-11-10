package school.faang.user_service.datatest;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserFilterAbout;
import school.faang.user_service.filter.user.UserFilterCity;
import school.faang.user_service.filter.user.UserFilterContact;
import school.faang.user_service.filter.user.UserFilterCountry;
import school.faang.user_service.filter.user.UserFilterEmail;
import school.faang.user_service.filter.user.UserFilterExperienceLessThanOrEqual;
import school.faang.user_service.filter.user.UserFilterExperienceMoreThanOrEqual;
import school.faang.user_service.filter.user.UserFilterName;
import school.faang.user_service.filter.user.UserFilterPhone;
import school.faang.user_service.filter.user.UserFilterSkill;

import java.util.ArrayList;
import java.util.List;

public class DataSubscription {
    public static UserFilterDto getUserFilterDtoInitValues(Integer pageNumber, Integer pageSize) {
        return new UserFilterDto("Name",
                "about",
                "email",
                "contact",
                "country",
                "city",
                "0162481239",
                "skill",
                1,
                100000,
                pageNumber,
                pageSize);
    }

    public static UserFilterDto getUserFilterDtoWrongValues(Integer pageNumber, Integer pageSize) {
        return new UserFilterDto("WrongWrongName",
                "WrongWrongabout",
                "WrongWrongemail",
                "WrongWrongcontact",
                "WrongWrongcountry",
                "WrongWrongcity",
                "WrongWrong0162481239",
                "WrongWrongskill",
                Integer.MAX_VALUE,
                Integer.MIN_VALUE,
                pageNumber,
                pageSize);
    }

    public static List<UserDto> getUserDtoList(int numberUsers) {
        List<UserDto> userDtos = new ArrayList<>();

        for (int i = 1; i <= numberUsers; i++) {
            userDtos.add(getNewUserDto(i));
        }
        return userDtos;
    }

    public static List<User> getUserList(int numberUserSuccessFilter, int numberUserNotSuccessFilter) {
        List<User> userList = new ArrayList<>();

        for (int y = 1; y <= numberUserSuccessFilter; y++) {
            userList.add(getNewUser(y));
        }

        for (int y = 1; y <= numberUserNotSuccessFilter; y++) {
            userList.add(getNewWrongDataUser(y));
        }

        return userList;
    }

    public static UserDto getNewUserDto(int i) {

        UserFilterDto userFilterDto = getUserFilterDtoInitValues(null, null);
        return new UserDto((long) i,
                userFilterDto.getNamePattern() + i,
                userFilterDto.getEmailPattern() + i);
    }

    public static User getNewUser(int i) {
        UserFilterDto userFilterDto = getUserFilterDtoInitValues(null, null);

        User tmpUser = new User(
                (long) i,
                userFilterDto.getNamePattern() + i,
                userFilterDto.getEmailPattern() + i,
                userFilterDto.getPhonePattern() + i,
                null,
                true,
                userFilterDto.getAboutPattern() + i,
                new Country(1, userFilterDto.getCountryPattern() + i, null),
                userFilterDto.getCityPattern() + i,
                i,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new ArrayList<>(),
                null,
                null,
                null,
                new ArrayList<>(),
                null,
                null,
                null,
                null
        );
        tmpUser.getContacts().add(new Contact(1, null, userFilterDto.getContactPattern() + 1, null));
        tmpUser.getContacts().add(new Contact(2, null, userFilterDto.getContactPattern() + 2, null));
        tmpUser.getSkills().add(new Skill(1, userFilterDto.getSkillPattern() + 1, null, null,
                null, null, null, null));
        tmpUser.getSkills().add(new Skill(2, userFilterDto.getSkillPattern() + 2, null, null,
                null, null, null, null));
        return tmpUser;
    }

    public static User getNewWrongDataUser(int i) {
        User tmpUser = new User(
                (long) Integer.MAX_VALUE - i,
                "useNa" + i,
                "wrong@gmail.com" + i,
                "123456789" + i,
                null,
                true,
                "wrongA" + i,
                new Country(1, "wrongCo" + i, null),
                "CiNew" + i,
                i,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new ArrayList<>(),
                null,
                null,
                null,
                new ArrayList<>(),
                null,
                null,
                null,
                null
        );
        tmpUser.getContacts().add(new Contact(1, null, "wrongCont" + 1, null));
        tmpUser.getContacts().add(new Contact(2, null, "wrongCont" + 2, null));
        tmpUser.getSkills().add(new Skill(1, "wrongSk" + 1, null, null,
                null, null, null, null));
        tmpUser.getSkills().add(new Skill(2, "wrongSk" + 2, null, null,
                null, null, null, null));

        return tmpUser;
    }

    public static List<UserFilter> getListUserFilters() {
        List<UserFilter> userFilters = new ArrayList<>();
        userFilters.add(new UserFilterAbout());
        userFilters.add(new UserFilterCity());
        userFilters.add(new UserFilterContact());
        userFilters.add(new UserFilterCountry());
        userFilters.add(new UserFilterEmail());
        userFilters.add(new UserFilterExperienceLessThanOrEqual());
        userFilters.add(new UserFilterExperienceMoreThanOrEqual());
        userFilters.add(new UserFilterName());
        userFilters.add(new UserFilterPhone());
        userFilters.add(new UserFilterSkill());
        return userFilters;
    }
}