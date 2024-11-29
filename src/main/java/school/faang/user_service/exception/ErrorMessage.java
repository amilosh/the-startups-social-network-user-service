package school.faang.user_service.exception;

public class ErrorMessage {
    public static final String USER_NOT_FOUND = "User not found with ID: \"%s\"";

    public static final String RECOMMENDATION_AUTHOR_NOT_FOUND = "THE AUTHOR OF THE RECOMMENDATION NOT FOUND - ";
    public static final String RECOMMENDATION_RECEIVER_NOT_FOUND = "THE RECEIVER OF THE RECOMMENDATION NOT FOUND - ";
    public static final String RECOMMENDATION_WRONG_TIME = "AUTHOR \"%s\" GAVE RECOMMENDATION FOR USER - \"%s\" LESS THEN \"%d\" MONTH AGO!";

    public static final String SKILL_NOT_EXIST = "THAT SKILL \"%s\" DOESN'T EXIST!";
    public static final String SKILL_OFFERS_IS_EMPTY = "THE SKILL OFFERS IS EMPTY";

    public static final String MINIO_UPLOAD_ERROR = "Error uploading file: %s";
    public static final String MINIO_DOWNLOAD_ERROR = "Error downloading file %s";
    public static final String MINIO_DELETED_ERROR = "Error deleting file %s";

    public static final String AVATAR_NOT_FOUND = "Avatar not found for user ID: %s";
    public static final String AVATAR_ALREADY_EXIST_ERROR = "Avatar is already uploaded for user ID: %s";
    public static final String AVATAR_PROCESS_ERROR = "Failed to process avatar image";

    public static final String DICE_BEAR_UNEXPECTED_ERROR = "Unexpected error occurred while retrieving avatar.";
    public static final String DICE_BEAR_GENERATING_ERROR = "Unexpected error while generating avatar.";
}
