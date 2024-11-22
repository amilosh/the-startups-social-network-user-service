package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundExceptionWithID;
import school.faang.user_service.filters.recommendation_request.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestServiceValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestServiceValidator validator;
    private final RecommendationRequestMapper mapper;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;


    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        log.info("Start creating RecommendationRequest: {}", recommendationRequestDto);
        validator.validateExistsRequesterAndReceiverInDatabase(recommendationRequestDto);
        validator.validateSixMonthRequestLimit(recommendationRequestDto);
        validator.validateExistsSkillsInDatabase(recommendationRequestDto);

        RecommendationRequest entityRecommendationRequest = mapper.toEntity(recommendationRequestDto);
        long idRecommendationRequest = entityRecommendationRequest.getId();
        List<SkillRequest> entitySkillRequests = recommendationRequestDto.getSkillIds().stream()
                .map(skillId -> skillRequestRepository.create(idRecommendationRequest, skillId))
                .toList();

        User entityRequester = userRepository.findById(recommendationRequestDto.getRequesterId()).get();
        User entityReceiver = userRepository.findById(recommendationRequestDto.getReceiverId()).get();

        entityRecommendationRequest
                .setRequester(entityRequester)
                .setReceiver(entityReceiver);
        if (entityRecommendationRequest.getSkills() == null) {
            entityRecommendationRequest.setSkills(new ArrayList<>());
        }
        entitySkillRequests.forEach(entityRecommendationRequest::addSkillRequest);
        RecommendationRequest savedEntity = recommendationRequestRepository.save(entityRecommendationRequest);

        log.info("The {} was created in the database", savedEntity);

        return mapper.toDTO(savedEntity);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        log.info("Start processing getRequests with filter: {}", filterDto);

        List<RecommendationRequest> recommendationRequestsAll = recommendationRequestRepository.findAll();
        List<RecommendationRequestFilter> suitableFilters = recommendationRequestFilters.stream()
                .filter(requestFilter -> requestFilter.isFilterApplicable(filterDto))
                .toList();

        List<RecommendationRequest> recommendationRequestsFiltered = recommendationRequestsAll.stream()
                .filter(recommendationRequest -> suitableFilters.stream()
                        .allMatch(suitableFilter -> suitableFilter.apply(recommendationRequest, filterDto)))
                .toList();

        log.info("Completed processing getRequests. Filtered results count: {}", recommendationRequestsFiltered.size());

        return mapper.allToDTO(recommendationRequestsFiltered);
    }

    public RecommendationRequestDto getRequest(Long id) {
        log.info("Start processing getRequest for ID: {}", id);

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExceptionWithID("The RecommendationRequest will not be found in the database", id));
        RecommendationRequestDto result = mapper.toDTO(recommendationRequest);

        log.info("Completed processing getRequest for ID: {}. Result: {}", id, result);

        return result;
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        log.info("Start processing rejectRequest for ID: {} with Rejection: {}", id, rejection);

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExceptionWithID("The RecommendationRequest will not be found in the database", id));

        RequestStatus status = recommendationRequest.getStatus();
        if (status != RequestStatus.PENDING) {
            throw new DataValidationException("The status of the RecommendationRequest by id-" + id + ", not pending");
        }
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());

        RecommendationRequestDto result = mapper.toDTO(recommendationRequest);

        log.info("Completed processing rejectRequest for ID: {}. Result: {}", id, result);

        return result;
    }
}
