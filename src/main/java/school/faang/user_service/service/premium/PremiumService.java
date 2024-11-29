package school.faang.user_service.service.premium;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PremiumServiceValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;

    public List<PremiumDto> getActivePremium() {
        log.info("Getting list of active premium from repository");
        return premiumRepository.getAllActivePremium().stream()
                .map(premiumMapper::toDto)
                .toList();
    }

    public List<PremiumDto> updatePremium(List<PremiumDto> premiums) {
        log.info("validate argument for null exception");
        PremiumServiceValidator.checkListForNull(premiums);

        log.info("updating premium");
        List<Premium> updatedPremium = new ArrayList<>();
        premiums.forEach(premiumDto -> {
            Premium premium = getPremium(premiumDto.getId());
            premiumMapper.update(premiumDto, premium);
            save(premium);
            updatedPremium.add(premium);
        });

        return updatedPremium.stream()
                .map(premiumMapper::toDto)
                .toList();
    }

    private Premium getPremium(long id) {
        log.info("trying to get premium entity from db");
        return premiumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }
    private void save(Premium premium){
        log.info("check argument for null exception");
        PremiumServiceValidator.checkPremiumNotNull(premium);
        premiumRepository.save(premium);
    }
}
