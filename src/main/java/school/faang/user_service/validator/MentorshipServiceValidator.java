package school.faang.user_service.validator;

import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

public class MentorshipServiceValidator {
    public static void testValidUserId(long userId, UserRepository userRepository) {
        if (userId < 0) {
            throw new IllegalArgumentException("Не верный id пользователя");
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Такого пользователя не существует");
        }
    }

    public static void testEmptyValue(List<User> users) {
        if (users == null) {
            throw new IllegalArgumentException("Отсутствует значение");
        }
    }
}
