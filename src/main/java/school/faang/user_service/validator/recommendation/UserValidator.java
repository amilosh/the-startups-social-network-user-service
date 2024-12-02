package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {
    public final static String AUTHOR_NOT_FOUND = "Author with id = %s does not exist";
    public final static String RECEIVER_NOT_FOUND = "Receiver with id = %s does not exist";

    private final UserRepository userRepository;


    public void existsAuthorById (Long authorId) {
        validateUserExist(authorId, String.format(AUTHOR_NOT_FOUND, authorId));
    }

    public void existsReceiverById(Long receiverId) {
        validateUserExist(receiverId, String.format(RECEIVER_NOT_FOUND, receiverId));
    }

    private void validateUserExist(Long userId, String errorMessage) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException(errorMessage);
        }
    }


}
