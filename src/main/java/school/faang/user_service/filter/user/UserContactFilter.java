package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class UserContactFilter implements Filter<User, UserFilterDto> {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getContactPattern() != null && !filter.getContactPattern().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        String contactPattern = filter.getContactPattern().toLowerCase();
        return users.filter(user -> user.getContacts() != null &&
                user.getContacts().stream()
                        .map(Contact::getContact)
                        .filter(contact -> contact != null)
                        .anyMatch(contact -> contact.toLowerCase().contains(contactPattern)));
    }
}
