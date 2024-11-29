package school.faang.user_service.validator;

import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

import java.util.List;

public class PremiumServiceValidator {
    public static void checkListForNull(List<PremiumDto> list){
        if (list == null) {
            throw new IllegalArgumentException("Пустой лист");
        }
    }
    public static void checkPremiumNotNull(Premium premium){
        if(premium==null){
            throw new IllegalArgumentException("Пустой объект premium");
        }
    }
}
