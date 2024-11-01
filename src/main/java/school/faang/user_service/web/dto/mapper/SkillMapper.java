package school.faang.user_service.web.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.web.dto.skill.SkillCandidateDto;
import school.faang.user_service.web.dto.skill.SkillDto;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper extends Mappable<Skill, SkillDto> {

    @Mapping(source = "users", target = "userIds", qualifiedByName = "map")
    SkillDto toDto(Skill skill);

    @Mapping(target = "users", ignore = true)
    Skill toEntity(SkillDto dto);

    @Named("map")
    default List<Long> map(List<User> users) {
        return users != null ? users.stream().map(User::getId).toList() : Collections.emptyList();
    }

    SkillCandidateDto toCandidateDto(Skill skill, long offersAmount);
}
