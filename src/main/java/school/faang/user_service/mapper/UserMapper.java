package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.DeactivatedUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUser(UserDto userDto);

    @Mapping(source = "settingGoals", target = "idsSettingGoals", qualifiedByName = "mapGoalsToListId")
    @Mapping(source = "goals", target = "idsGoals", qualifiedByName = "mapGoalsToListId")
    @Mapping(source = "skills", target = "idsSkills", qualifiedByName = "mapSkillsToListId")
    @Mapping(source = "mentors", target = "idsMentors", qualifiedByName = "mapMentorsToListId")
    @Mapping(source = "ownedEvents", target = "idsOwnedEvents", qualifiedByName = "mapOwnedEventsToListId")
    DeactivatedUserDto toDeactivatedUserDto(User user);

    @Mapping(target = "settingGoals", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(target = "ownedEvents", ignore = true)
    User toEntity(DeactivatedUserDto deactivatedUserDto);

    @Named("mapGoalsToListId")
    default List<Long> mapGoalsToListId(List<Goal> goals) {
        if (goals == null || goals.isEmpty()) {
            return Collections.emptyList();
        }
        return goals.stream()
                .map(goal -> goal.getId())
                .toList();
    }

    @Named("mapSkillsToListId")
    default List<Long> mapSkillsToListId(List<Skill> skills) {
        if (skills == null) {
            return Collections.emptyList();
        }
        return skills.stream()
                .map(skill -> skill.getId())
                .toList();
    }

    @Named("mapMentorsToListId")
    default List<Long> mapMentorsToListId(List<User> mentors) {
        if (mentors == null) {
            return Collections.emptyList();
        }
        return mentors.stream()
                .map(mentor -> mentor.getId())
                .toList();
    }

    @Named("mapOwnedEventsToListId")
    default List<Long> mapOwnedEventsToListId(List<Event> ownedEvents) {
        if (ownedEvents == null) {
            return Collections.emptyList();
        }
        return ownedEvents.stream()
                .map(event -> event.getId())
                .toList();
    }
}