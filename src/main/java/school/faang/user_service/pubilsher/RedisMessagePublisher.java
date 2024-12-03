package school.faang.user_service.pubilsher;


public interface RedisMessagePublisher {
    void publish(Long fundRaisedId);
}
