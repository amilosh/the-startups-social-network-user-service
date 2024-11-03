package school.faang.user_service.utilities;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UrlUtils {
    public static final String MAIN_URL = "/api/user-service";
    public static final String ID = "/{id}";
    public static final String REQUEST = "/request";
    public static final String CREATE_REQUEST = "/create-request";
    public static final String ACCEPT = "/accept";
    public static final String REJECT = "/reject";
    public static final String REQUESTS_FILTER = "/getRequestsByFilter";
}