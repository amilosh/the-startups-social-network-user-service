package school.faang.user_service.exception;

public class ErrorMessage {
    public static final String RECOMMENDATION_EMPTY_CONTENT = "THE CONTENT OF THE RECOMMENDATION CANNOT BE EMPTY!";
    public static final String RECOMMENDATION_EMPTY_AUTHOR = "THE AUTHOR OF THE RECOMMENDATION IS EMPTY";
    public static final String RECOMMENDATION_AUTHOR_NOT_FOUND = "THE AUTHOR OF THE RECOMMENDATION NOT FOUND - ";

    public static final String RECOMMENDATION_EMPTY_RECEIVER = "THE RECEIVER OF THE RECOMMENDATION IS EMPTY";
    public static final String RECOMMENDATION_RECEIVER_NOT_FOUND = "THE RECEIVER OF THE RECOMMENDATION NOT FOUND - ";

    public static final String RECOMMENDATION_WRONG_TIME = "AUTHOR \"%s\" GAVE RECOMMENDATION FOR USER - \"%s\" LESS THEN \"%d\" MONTH AGO!";
    public static final String SKILL_NOT_EXIST = "THAT SKILL \"%s\" DOESN'T EXIST!";
    public static final String SKILL_OFFERS_IS_EMPTY = "THE SKILL OFFERS IS EMPTY";

}
