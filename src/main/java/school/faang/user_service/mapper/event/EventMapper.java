package school.faang.user_service.mapper.event;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.promotion.PromotedEventResponseDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.mapper.skill.SkillMapper;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = SkillMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EventMapper {
    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "status", ignore = true)
    Event toEntity(EventDto eventDto);

    List<Event> toEntity(List<EventDto> eventsDto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    EventDto toDto(Event event);

    List<EventDto> toDto(List<Event> events);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "promotions", target = "promotionTariff", qualifiedByName = "mapTariff")
    @Mapping(source = "promotions", target = "numberOfViews", qualifiedByName = "mapNumberOfViews")
    PromotedEventResponseDto toPromotedEventResponseDto(Event event);

    @Named("mapTariff")
    default String mapTariff(List<EventPromotion> eventPromotions) {
        Optional<EventPromotion> promotionOpt = getActivePromotion(eventPromotions);
        return promotionOpt
                .map(eventPromotion -> eventPromotion.getPromotionTariff().toString())
                .orElse(null);
    }

    @Named("mapNumberOfViews")
    default Integer mapNumberOfViews(List<EventPromotion> eventPromotions) {
        Optional<EventPromotion> promotionOpt = getActivePromotion(eventPromotions);
        return promotionOpt
                .map(EventPromotion::getNumberOfViews)
                .orElse(null);
    }

    private Optional<EventPromotion> getActivePromotion(List<EventPromotion> eventPromotions) {
        return eventPromotions
                .stream()
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .findFirst();
    }
}