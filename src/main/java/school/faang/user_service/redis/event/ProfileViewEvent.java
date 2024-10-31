package school.faang.user_service.redis.event;

public record ProfileViewEvent(Long watchingId, Long watchedId) {}