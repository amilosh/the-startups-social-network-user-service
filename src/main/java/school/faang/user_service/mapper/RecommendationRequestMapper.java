package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skills", target = "skills", qualifiedByName = "mapSkillsToIds")
    //todo Не работает dateFormat, why?
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    RecommendationRequestDto toDto(RecommendationRequest entity);

    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    //todo видимо правильно тут проигнорировать skills, так как SkillRequest из skills создаются в Service после создания RecommendationRequest
    @Mapping(target = "skills", ignore = true)
    //todo правильно ли игнорировать id, createdAt, updatedAt? Они ведь создадутся в БД автоматически.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto dto);

    @Named("mapSkillsToIds")
    default List<Long> mapSkillsToIds(List<SkillRequest> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(SkillRequest::getSkill)
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}