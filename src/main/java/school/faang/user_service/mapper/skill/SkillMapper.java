package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    SkillDto toDto(Skill skill);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "guarantees", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Skill toEntity(SkillDto skillDto);

    List<SkillDto> toDto(List<Skill> skills);

    List<Skill> toEntity(List<SkillDto> skillDtos);
}
