package school.faang.user_service.publisher.recommendation;

public interface MessagePublish<T> {
    void publish(T event);
}
