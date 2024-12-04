package school.faang.user_service.exception;

public class ErrorMessages {
    public static final String M_FOLLOW_YOURSELF = "Can not add following. You try to follow to yourself.";
    public static final String M_FOLLOW_EXIST = "Can not add follow. The following relation is already exist";
    public static final String M_UNFOLLOW_YOURSELF = "Can not delete unfollowing. You try to unfollow yourself.";
    public static final String M_FOLLOW_DOES_NOT_EXIST = "Can not delete unfollowing. The following relation doesn't exist. Choose other.";
}