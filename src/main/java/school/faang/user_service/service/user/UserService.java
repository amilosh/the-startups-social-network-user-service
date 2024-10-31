package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import school.faang.user_service.entity.User;


@Transactional
public User getUserById(Long userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User with this id does not exist in the database"));
}
