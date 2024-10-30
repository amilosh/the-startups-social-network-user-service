package school.faang.user_service.exception;

public class ErrorMessage {
    public static final String RECOMMENDATION_CONTENT = "THE TEXT OF THE RECOMMENDATION CANNOT BE EMPTY";
    public static final String RECOMMENDATION_AUTHOR = "THE AUTHOR OF THE RECOMMENDATION IS NOT SPECIFIED";
    public static final String RECOMMENDATION_RECEIVER = "THE RECEIVER OF THE RECOMMENDATION IS NOT SPECIFIED";
    public static final String RECOMMENDATION_TIME_LIMIT = "Author id  \"%s\" did recommendation for user id = \"%s\" less than \"%d\" months ago";
    public static final String SKILL_NOT_EXIST = "SKILL WITH NAME \"%s\" DOES NOT EXIST IN SYSTEM";
}
