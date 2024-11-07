package school.faang.user_service.web.dto.mapper;

public interface Mappable<E, D> {

    E toEntity(D dto);

    D toDto(E entity);

}
