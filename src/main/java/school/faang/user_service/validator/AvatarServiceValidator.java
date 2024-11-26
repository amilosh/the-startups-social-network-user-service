package school.faang.user_service.validator;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
@Component
public class AvatarServiceValidator {
    public void checkUser(User user){
        if(user==null||user.getUsername()==null||user.getUsername().isBlank()){
            throw new IllegalArgumentException("Пустой пользователь или его имя");
        }
    }
}
