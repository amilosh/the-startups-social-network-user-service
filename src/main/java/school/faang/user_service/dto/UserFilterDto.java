package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    @Pattern(regexp = ".*", message = "Имя не может быть пустым")
    private String namePattern;

    @Pattern(regexp = ".*", message = "Об не может быть пустым")
    private String aboutPattern;

    @Pattern(regexp = ".+@.+\\..+", message = "Email должен быть валидным")
    private String emailPattern;

    @Pattern(regexp = ".*", message = "Контакт не должен быть пустым")
    private String contactPattern;

    @Pattern(regexp = ".*", message = "Страна не должна быть пустой")
    private String countryPattern;

    @Pattern(regexp = ".*", message = "Город не должен быть пустым")
    private String cityPattern;

    @Pattern(regexp = "\\+?[0-9]*", message = "Номер телефона должен быть валидным")
    private String phonePattern;

    @Pattern(regexp = ".*", message = "Скилл не должен быть пустым")
    private String skillPattern;

    @Min(value = 0, message = "Опыт не должен быть меньше 0")
    private int experienceMin;

    @Min(value = 0, message = "Максимальный опыт не должен быть 0")
    private int experienceMax;

    @Min(value = 0, message = "Номер страницы не должен быть 0")
    private int page;

    @Min(value = 1, message = "Размер страницы не должен быть 1")
    private int pageSize;
}
