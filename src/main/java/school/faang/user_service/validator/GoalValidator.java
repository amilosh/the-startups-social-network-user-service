package school.faang.user_service.validator;

import org.springframework.stereotype.Component;

@Component
public class GoalValidator {
    public void validateId(long id){
        if(id<0){
            throw new IllegalArgumentException("Не валидный id цели");
        }
    }
}
