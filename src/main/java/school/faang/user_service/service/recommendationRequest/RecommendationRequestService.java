package school.faang.user_service.service.recommendationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestRejectionDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendationRequest.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendationRequest.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.SkillRequestService.SkillRequestService;
import school.faang.user_service.validator.recommendationRequest.RecommendationRequestValidator;

import java.util.List;

import static school.faang.user_service.entity.RequestStatus.REJECTED;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final SkillRequestService skillRequestService;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        log.info("Creating a recommendation request for requester ID: {} and recipient ID: {}",
                recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId());

        recommendationRequestValidator.validateCreate(recommendationRequestDto);

        RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.toEntity(recommendationRequestDto);
        recommendationRequestRepository.save(recommendationRequestEntity);
        log.info("Recommendation request saved with ID: {}", recommendationRequestEntity.getId());

        skillRequestService.saveSkillRequests(recommendationRequestEntity, recommendationRequestDto.getSkillsId());

        log.info("Recommendation request successfully created with ID: {}", recommendationRequestEntity.getId());
        return recommendationRequestMapper.toDto(recommendationRequestEntity);
    }

    public List<RecommendationRequestDto> getRequest(RecommendationRequestFilterDto recommendationRequestFilterDto) {
        log.info("Retrieving recommendation requests with filters: {}", recommendationRequestFilterDto);

        recommendationRequestValidator.validateOfFilterAvailability(recommendationRequestFilterDto);

        List<RecommendationRequest> recommendations = recommendationRequestRepository.findAll();
        log.info("Found {} recommendation requests before applying filters", recommendations.size());

        recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(recommendationRequestFilterDto))
                .forEach(filter -> {
                    log.info("Applying filter: {}", filter.getClass().getSimpleName());
                    filter.apply(recommendations.stream(), recommendationRequestFilterDto);
                });

        List<RecommendationRequestDto> result = recommendations.stream()
                .map(recommendationRequestMapper::toDto)
                .toList();

        log.info("Returned {} recommendation requests after applying filters", result.size());
        return result;
    }

    public RecommendationRequestDto getRequest(long id) {
        log.info("Retrieving a recommendation request with ID: {}", id);
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Recommendation request with ID {} not found", id);
                    return new DataValidationException("Recommendation request with ID " + id + " not found");
                });
        log.info("Recommendation request found with ID: {}", id);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public RecommendationRequestDto rejectRequest(long id, RecommendationRequestRejectionDto recommendationRequestRejectionDto) {
        log.info("Rejecting recommendation request with ID: {}", id);

        RecommendationRequest recommendationRequest = recommendationRequestValidator.validateRequestExists(id,
                recommendationRequestRepository);

        recommendationRequest.setStatus(REJECTED);
        recommendationRequest.setRejectionReason(recommendationRequestRejectionDto.getRejectionReason());
        recommendationRequestRepository.save(recommendationRequest);
        log.info("Recommendation request with ID: {} successfully rejected", id);

        return recommendationRequestMapper.toDto(recommendationRequest);
    }
}


