package school.faang.user_service.filter.userFilter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

@RequiredArgsConstructor
public class ContactPatternFilter implements UserFilter {
    private final Contact pattern;

    @Override
    public boolean apply(User user) {
        return user.getContacts() != null && user.getContacts().contains(pattern);
    }
}
