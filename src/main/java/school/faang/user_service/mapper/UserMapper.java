package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "mentees", target = "menteeId", qualifiedByName = "mapToMenteeId")
    @Mapping(source = "mentors", target = "mentorId", qualifiedByName = "mapToMentorId")
    @Mapping(source = "skills", target = "skillsId", qualifiedByName = "mapToSkillsId")
    UserDto toDto(User user);

    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(target = "skills", ignore = true)
    User toEntity(UserDto userDto);
    List<UserDto> toDto (List<User> users);
    List<User> toEntity (List<UserDto> usersDto);
@Named("mapToMenteeId")
    default List<Long> mapToMenteeId(List<User> mentees) {
        return mentees.stream().map(User::getId).toList();
    }
@Named("mapToMentorId")
    default List<Long> mapToMentorId(List<User> mentors) {
        return mentors.stream().map(User::getId).toList();
    }
@Named("mapToSkillsId")
    default List<Long> mapToSkillsId(List<User> skills) {
        return skills.stream().map(User::getId).toList();
    }

}
