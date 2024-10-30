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
    public static final String REGISTER = "/register";
    public static final String UNREGISTER = "/unregister";
    public static final String PARTICIPANTS = "/participants";
    public static final String AMOUNT = "/amount";
}
