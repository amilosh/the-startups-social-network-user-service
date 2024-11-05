package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.user.UserResourceNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserService {
    private UserRepository userRepository;

    public User getByIdOrThrow(Long id) {
        User user = userRepository.getById(id) ;

        if (user == null)
            throw new UserResourceNotFoundException("Не существует пользователя в БД по id = " + id);

        return user;
    }
}
