package school.faang.user_service.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UpdateUsersRankDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final static int BATCH_SIZE = 50;
    @PersistenceContext
    private final EntityManager entityManager;
    private final UserRepository userRepository;

    public Optional<User> findById(long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public ResponseEntity<Void> updateUsersRankByUserIds(UpdateUsersRankDto userDto) {
        log.info("batch is starting {}", userDto);
        int batchCounter = 1;
        for (Map.Entry<Long, Double> userNewRank : userDto.getUsersRankByIds().entrySet()) {
            if (userNewRank.getValue() != 0.0) {
                BigDecimal value = BigDecimal.valueOf(userNewRank.getValue());
                double roundedValue = value.setScale(2, RoundingMode.HALF_UP).doubleValue();
                try {
                    userRepository.updateUserRankByUserId(userNewRank.getKey(), roundedValue);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (roundedValue > userDto.getHalfUserRank()) {
                        userRepository.updateUserRankByUserIdToMax(userNewRank.getKey(), BigDecimal.valueOf(userDto.getMaximumUserRating()));
                    } else {
                        userRepository.updateUserRankByUserIdToMin(userNewRank.getKey(), BigDecimal.valueOf(userDto.getMinimumUserRating()));
                    }
                }
                batchCounter++;
            }
            if (batchCounter % BATCH_SIZE == 0) {
                flushAndClear();
            }
        }
        updatePassiveUsersRating(userDto);
        log.info("users rank success updated!");
        return ResponseEntity.ok().build();
    }

    @Transactional
    public void updatePassiveUsersRating(UpdateUsersRankDto userDto) {
        BigDecimal maxPossibleRating = BigDecimal.valueOf(userDto.getMaximumGrowthRating() * userDto.getRatingGrowthIntensive());
        BigDecimal roundedValue = maxPossibleRating.setScale(2, RoundingMode.HALF_UP);
        Set<Long> activeUsersIds = userDto.getUsersRankByIds().keySet();
        userRepository.updatePassiveUsersRatingWhichRatingLessThanRating(roundedValue.doubleValue(), activeUsersIds);
        userRepository.updatePassiveUsersRatingWhichRatingMoreThanRating(roundedValue.doubleValue(), activeUsersIds);
        flushAndClear();
    }

    @Transactional
    public void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
