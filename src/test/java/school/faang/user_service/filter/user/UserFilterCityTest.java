package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.datatest.DataSubscription;
import school.faang.user_service.dto.UserFilterDto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserFilterCityTest {

    @InjectMocks
    private UserFilterCity filter;

    @Test
    void applyWithInitValueSuccessTest() {
        UserFilterDto userFilterDto = DataSubscription.getUserFilterDtoWrongValues(null, null);
        userFilterDto.setCityPattern(DataSubscription.getUserFilterDtoInitValues(null, null).getCityPattern());

        assertTrue(filter.apply(DataSubscription.getNewUser(1), userFilterDto),
                "EF001 - should be true");
    }

    @Test
    void applyWithNullValueSuccessTest() {
        UserFilterDto userFilterDto = DataSubscription.getUserFilterDtoWrongValues(null, null);
        userFilterDto.setCityPattern(null);

        assertTrue(filter.apply(DataSubscription.getNewUser(1), userFilterDto),
                "EF002 - should be true");
    }

    @Test
    void applyWrongValueFailTest() {
        UserFilterDto userFilterDto = DataSubscription.getUserFilterDtoInitValues(null, null);
        userFilterDto.setCityPattern(DataSubscription.getUserFilterDtoWrongValues(null, null).getCityPattern());
        assertFalse(filter.apply(DataSubscription.getNewUser(1), userFilterDto),
                "EF003 - should be false");
    }
}