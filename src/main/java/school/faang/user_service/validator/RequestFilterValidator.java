package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.exception.InvalidRequestFilterException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestFilterValidator {

    public void validateNullFilter(RequestFilterDto filters) {
        if (filters == null) {
            log.warn("Filter cannot be null.");
            throw new InvalidRequestFilterException("Filter cannot be null.");
        }
    }
}
