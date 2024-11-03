package school.faang.user_service.utilities;

import lombok.experimental.UtilityClass;

<<<<<<< HEAD
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
}
=======
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
>>>>>>> e88fd24b4 (BJS2-40479 completed without Tests)
