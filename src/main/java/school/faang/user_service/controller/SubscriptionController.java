package school.faang.user_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.service.SubscriptionService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/follow")
    public ResponseEntity<String> followUser(@RequestParam Long followerId, @RequestParam Long followeeId) {
        log.info("Попытка подписки пользователя. followerId={}, followeeId={}", followerId, followeeId);

        if (followerId == null || followeeId == null) {
            return ResponseEntity.badRequest().body("ID подписчика и ID пользователя, на которого подписываются, не должны быть null.");
        }

        if (followerId.equals(followeeId)) {
            log.info("Попытка самоподписки. followerId={}", followerId);
            return ResponseEntity.badRequest().body("Вы не можете подписаться на себя.");
        }

        try {
            if (subscriptionService.subscriptionExists(followerId, followeeId)) {
                log.warn("Подписка уже существует: followerId={}, followeeId={}", followerId, followeeId);
                return ResponseEntity.badRequest().body("Подписка уже существует.");
            }
            subscriptionService.followUser(followerId, followeeId);
            log.info("Успешно подписались на пользователя. followerId={}, followeeId={}", followerId, followeeId);
            return ResponseEntity.ok("Успешно подписаны на пользователя.");
        } catch (Exception e) {
            log.error("Ошибка при подписке на пользователя: followerId={}, followeeId={}", followerId, followeeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Не удалось подписаться на пользователя.");
        }
    }

    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(@RequestParam Long followerId, @RequestParam Long followeeId) {
        if (followerId == null || followeeId == null) {
            return ResponseEntity.badRequest().body("ID подписчика и ID пользователя, от которого отписываются, не должны быть null.");
        }

        try {
            subscriptionService.unfollowUser(followerId, followeeId);
            log.info("Пользователь с ID {} успешно отписался от пользователя с ID {}", followerId, followeeId);
            return ResponseEntity.ok("Успешно отписались.");
        } catch (IllegalArgumentException ex) {
            log.warn("Не удалось отписаться от пользователя: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (DataAccessException ex) {
            log.error("Произошла ошибка базы данных при отписке от пользователя", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка базы данных.");
        }
    }

    @GetMapping("/followers")
    public ResponseEntity<List<UserDTO>> getFollowers(
        @RequestParam Long userId,
        @RequestBody(required = false) UserFilterDTO filter) {

        if (userId == null) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        if (filter != null && !isValidFilter(filter)) {
            log.warn("Некорректные параметры фильтра: минимальный опыт превышает максимальный опыт.");
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<UserDTO> followers = subscriptionService.getFollowers(userId, Optional.ofNullable(filter).orElse(new UserFilterDTO()));
        log.info("Получено {} подписчиков для пользователя с ID {}", followers.size(), userId);
        return ResponseEntity.ok(followers);
    }

    private boolean isValidFilter(UserFilterDTO filter) {
        return filter.getExperienceMin() == null || filter.getExperienceMax() == null ||
            filter.getExperienceMin() <= filter.getExperienceMax();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Произошло исключение IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
        log.error("Ошибка доступа к базе данных: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка базы данных.");
    }
}
