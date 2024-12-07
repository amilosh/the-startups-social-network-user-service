package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserGoogleInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGoogleInfoRepository extends JpaRepository<UserGoogleInfo, Long> {
    Optional<UserGoogleInfo> findBySub(String sub);

    @Query("SELECT u FROM UserGoogleInfo u WHERE u.user.id IN :userIds")
    List<UserGoogleInfo> findAllByUserIds(List<Long> userIds);

    Optional<UserGoogleInfo> findByUser(User user);
}
