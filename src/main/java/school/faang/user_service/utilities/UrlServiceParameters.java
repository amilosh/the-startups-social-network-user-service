package school.faang.user_service.utilities;

public class UrlServiceParameters {
    public static final String SYSTEM_ID = "001-UserService";
    public static final String FOLLOWING_SERVICE_URL = "/following";
    public static final String FOLLOWING_ADD = "/add/";
    public static final String FOLLOWING_DELETE = "/delete/";
    public static final String FOLLOWING_FILTER = "/followee-filter/followeeId=";
    public static final String FOLLOWING_PARAMETERS = "followerId={followerId}&followeeId={followeeId}";
    public static final String FOLLOWING_COUNT = "/count-followers/followerId=";
}