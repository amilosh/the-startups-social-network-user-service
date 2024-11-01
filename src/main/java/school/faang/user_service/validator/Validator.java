package school.faang.user_service.validator;


public interface Validator<T> {
    boolean isValid(T dto);

    String getMessage();
}
