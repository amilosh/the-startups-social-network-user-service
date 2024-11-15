package school.faang.user_service.utilities;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UrlUtils {
    public static final String MAIN_URL = "/api/user-service";
    public static final String V1 = "/v1";
    public static final String EVENTS = "/events";
    public static final String ID = "/{id}";
    public static final String PARTICIPANTS = "/participants";
    public static final String AMOUNT = "/amount";
    public static final String MENTORSHIP = "/mentorship";
    public static final String REQUEST = "/request";
    public static final String ACCEPT = "/accept";
    public static final String REJECT = "/reject";
    public static final String USERS = "/users";

    public static final String SKILLS = "/skills";
    public static final String RECOMMENDATION = "/recommendation";
    public static final String RECEIVER_SKILL_OFFERS = "/receiver-skill-offers";
    public static final String AUTHOR_SKILL_OFFERS = "/author-skill-offers";
}