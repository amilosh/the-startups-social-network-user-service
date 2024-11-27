package school.faang.user_service.mapper.event;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.promotion.PromotedEventResponseDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.mapper.skill.SkillMapper;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = SkillMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    Event toEntity(EventDto eventDto);

    List<Event> toEntity(List<EventDto> eventsDto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    EventDto toDto(Event event);

    List<EventDto> toDto(List<Event> events);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "promotion", target = "promotionTariff", qualifiedByName = "mapTariff")
    @Mapping(source = "promotion", target = "numberOfViews", qualifiedByName = "mapNumberOfViews")
    PromotedEventResponseDto toPromotedEventResponseDto(Event event);

    @Named("mapTariff")
    default String mapTariff(EventPromotion eventPromotion) {
        return Optional.ofNullable(eventPromotion)
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .map(promotion -> promotion.getPromotionTariff().toString())
                .orElse(null);
    }

    @Named("mapNumberOfViews")
    default Integer mapNumberOfViews(EventPromotion eventPromotion) {
        return Optional.ofNullable(eventPromotion)
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .map(EventPromotion::getNumberOfViews)
                .orElse(null);
    }

    default Optional<EventPromotion> getActivePromotion(EventPromotion eventPromotion) {
        return Optional.ofNullable(eventPromotion)
                .filter(promotion -> promotion.getNumberOfViews() > 0);
    }

}