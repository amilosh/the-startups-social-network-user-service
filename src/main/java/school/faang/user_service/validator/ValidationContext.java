package school.faang.user_service.validator;

import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.UserService;

public record ValidationContext(MentorshipRequestService mentorshipRequestService, UserService userService) {}
