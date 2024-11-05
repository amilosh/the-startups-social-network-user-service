package school.faang.user_service.exception.recommendation;

public class ErrorMessage {
    public static final String RECOMMENDATION_CONTENT = "THE TEXT OF THE RECOMMENDATION CANNOT BE EMPTY";
    public static final String RECOMMENDATION_AUTHOR = "THE AUTHOR OF THE RECOMMENDATION IS NOT SPECIFIED";
    public static final String RECOMMENDATION_RECEIVER = "THE RECEIVER OF THE RECOMMENDATION IS NOT SPECIFIED";
    public static final String RECOMMENDATION_TIME_LIMIT = "AUTHOR ID  \"%s\" DID RECOMMENDATION FOR USER ID = \"%s\" LESS THAN \"%d\" MONTHS AGO";
    public static final String SKILL_NOT_EXIST = "SKILL WITH NAME \"%s\" DOES NOT EXIST IN SYSTEM";
    public static final String NO_SKILL_OFFERS = "NO SKILL OFFERS FOUND FOR RECOMMENDATION";
}