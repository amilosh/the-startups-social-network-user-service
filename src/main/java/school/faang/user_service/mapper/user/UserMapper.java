package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.control.MappingControl;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "mentees", target = "menteesIds", qualifiedByName = "mentees")
    @Mapping(source = "mentors", target = "mentorsIds", qualifiedByName = "mentors")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "StringDate")
    @Mapping(source = "followers", target = "followersIds", qualifiedByName = "followers")
    @Mapping(source = "followees", target = "followeesIds", qualifiedByName = "followees")
    @Mapping(source = "goals", target = "goalsIds", qualifiedByName = "goals")
    @Mapping(source = "skills", target = "skillsIds", qualifiedByName = "skills")
    UserDto toDto(User user);

    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "Date")
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followees", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "skills", ignore = true)
    User toUser(UserDto dto);

    @Named("mentees")
    default List<Long> getMenteesIds(List<User> user) {
        return user.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Named("followers")
    default List<Long> getFollowersIds(List<User> user) {
        return user.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Named("followees")
    default List<Long> getFolloweesIds(List<User> user) {
        return user.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Named("setGoals")
    default List<Long> getSetGoalsIds(List<Goal> user) {
        return user.stream()
                .map(Goal::getId)
                .collect(Collectors.toList());
    }

    @Named("goals")
    default List<Long> getGoalsIds(List<Goal> user) {
        return user.stream()
                .map(Goal::getId)
                .collect(Collectors.toList());
    }

    @Named("skills")
    default List<Long> getSkillsIds(List<Skill> user) {
        return user.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }

    @Named("mentors")
    default List<Long> getMentorsIds(List<User> user) {
        return user.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Named("StringDate")
    default String createdAtString(LocalDateTime Date) {
        return Date.toString().formatted(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Named("Date")
    default LocalDateTime createdAtData(String createdAt) {
        return LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
