package school.faang.user_service.repository;

import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.util.List;

@Repository
public interface EventParticipationRepository {
    void saveParticipationRepository(Long eventId, Long userId);

    void register(Long eventId, Long userId);

    void unregister(Long eventId, Long userId);

    List<User> findAllParticipantsByEventId(Long eventId);

    int countParticipants(Long eventId);


}
