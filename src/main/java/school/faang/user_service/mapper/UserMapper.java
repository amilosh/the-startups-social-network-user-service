package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "settingGoals", target = "idsSettingGoals", qualifiedByName = "mapGoalsToListId")
    @Mapping(source = "goals", target = "idsGoals", qualifiedByName = "mapGoalsToListId")
    @Mapping(source = "skills", target = "idsSkills", qualifiedByName = "mapSkillsToListId")
    @Mapping(source = "mentors", target = "idsMentors", qualifiedByName = "mapMentorsToListId")
    UserDto toUserDto(User user);

    @Mapping(target = "settingGoals", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    User toEntity(UserDto userDto);

    @Named("mapGoalsToListId")
    default List<Long> mapGoalsToListId(List<Goal> goals) {
        return goals.stream()
                .map(goal -> goal.getId())
                .toList();
    }

    @Named("mapSkillsToListId")
    default List<Long> mapSkillsToListId(List<Skill> skills) {
        return skills.stream()
                .map(skill -> skill.getId())
                .toList();
    }

    @Named("mapMentorsToListId")
    default List<Long> mapMentorsToListId(List<User> mentors){
        return mentors.stream()
                .map(mentor -> mentor.getId())
                .toList();
    }
}