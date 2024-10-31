package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "mentees", target = "menteesId", qualifiedByName = "mapToMenteesId")
    @Mapping(source = "mentors", target = "mentorsId", qualifiedByName = "mapToMentorsId")
    @Mapping(source = "skills", target = "skillsId", qualifiedByName = "mapToSkillsId")
    UserDto toDto(User user);

    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(target = "skills", ignore = true)
    User toEntity(UserDto userDto);


    List<UserDto> toDto (List<User> users);

    List<User> toEntity (List<UserDto> usersDto);
@Named("mapToMenteesId")
    default List<Long> mapToMenteesId(List<User> mentees) {
    if (mentees == null) {
        return new ArrayList<>();
    }
    return mentees.stream().map(User::getId).toList();
    }
@Named("mapToMentorsId")
    default List<Long> mapToMentorsId(List<User> mentors) {
    if (mentors == null) {
        return new ArrayList<>();
    }
    return mentors.stream().map(User::getId).toList();
    }
@Named("mapToSkillsId")
    default List<Long> mapToSkillsId(List<Skill> skills) {
    if (skills == null) {
        return new ArrayList<>();
    }
        return skills.stream().map(Skill::getId).toList();
    }
}
