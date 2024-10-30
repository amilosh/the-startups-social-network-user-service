package school.faang.user_service.service.user_filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {

   default boolean isApplicable(UserFilterDto filter){
       return filter != null;
   }

    Stream<User> apply(Stream<User> users, UserFilterDto filter);
}
