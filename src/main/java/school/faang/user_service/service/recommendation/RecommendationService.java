package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.UserSkillGuaranteeService;
import school.faang.user_service.service.validation.RecommendationValidation;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final UserSkillGuaranteeService userSkillGuaranteeService;
    private final UserService userService;
    private final SkillOfferService skillOfferService;
    private final RecommendationValidation recommendationValidation;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationValidation.checkTimeInterval(recommendationDto);
        recommendationValidation.checkSkillsExist(recommendationDto);
        recommendationValidation.checkSkillsUnique(recommendationDto);
        recommendationValidation.checkRequest(recommendationDto);

        User receiver = userService.findById(recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Receiver not found"));
        User guarantor = userService.findById(recommendationDto.getAuthorId())
                .orElseThrow(() -> new DataValidationException("Guarantor not found"));
        Long recommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        skillOfferService.saveSkillOffers(recommendationDto.getSkillOffers(), recommendationId);
        userSkillGuaranteeService.createGuarantees(recommendationDto.getSkillOffers(), guarantor, receiver);
        recommendationDto.setId(recommendationId);
        return recommendationDto;
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        recommendationValidation.checkId(recommendationDto);
        recommendationValidation.checkTimeInterval(recommendationDto);
        recommendationValidation.checkSkillsExist(recommendationDto);
        recommendationValidation.checkSkillsUnique(recommendationDto);

        User receiver = userService.findById(recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Receiver not found"));
        User guarantor = userService.findById(recommendationDto.getAuthorId())
                .orElseThrow(() -> new DataValidationException("Guarantor not found"));
        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        skillOfferService.deleteAllByRecommendationId(recommendationDto.getId());
        skillOfferService.saveSkillOffers(recommendationDto.getSkillOffers(), recommendationDto.getId());
        userSkillGuaranteeService.createGuarantees(recommendationDto.getSkillOffers(), guarantor, receiver);
        return recommendationDto;
    }

    public boolean recommendationExists(long id) {
        return recommendationRepository.findById(id).isPresent();
    }

    public long delete(long id) {
        recommendationRepository.deleteById(id);
        return id;
    }

    public List<RecommendationDto> getAllUserRecommendations(long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recommendationRepository.findAllByReceiverId(userId, pageable)
                .map(recommendationMapper::toDto).stream().toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recommendationRepository.findAllByAuthorId(authorId, pageable)
                .map(recommendationMapper::toDto).stream().toList();
    }
}
