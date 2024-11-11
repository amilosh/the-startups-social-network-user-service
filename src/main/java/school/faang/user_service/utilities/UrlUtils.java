package school.faang.user_service.utilities;

import lombok.experimental.UtilityClass;

/**
 * Класс содержит константы для формирования url.
 *
 */
@UtilityClass
public class UrlUtils {
    public static final String MAIN_URL = "/api/user-service";
    public static final String V1 = "/v1";
    public static final String EVENTS = "/events";
    public static final String ID = "/{id}";
    public static final String PARTICIPANTS = "/participants";
    public static final String AMOUNT = "/amount";

    public static final String SYSTEM_ID = "001-UserService";
    public static final String FOLLOWING_SERVICE_URL = "/following";
    public static final String FOLLOWING_ADD = "/add/";
    public static final String FOLLOWING_DELETE = "/delete/";
    public static final String FOLLOWING_FILTER = "/followee-filter/followeeId=";
    public static final String FOLLOWING_PARAMETERS = "followerId={followerId}&followeeId={followeeId}";
    public static final String FOLLOWING_COUNT = "/count-followers/followerId=";

}
