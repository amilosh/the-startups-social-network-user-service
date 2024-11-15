package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
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
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestServiceValidator validator;
    private final RecommendationRequestMapper mapper;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;


    public void create(RecommendationRequestDto recommendationRequestDto) {
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

        recommendationRequestRepository.save(entityRecommendationRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        List<RecommendationRequest> recommendationRequestsAll = recommendationRequestRepository.findAll();
        List<RecommendationRequestFilter> suitableFilters = recommendationRequestFilters.stream()
                .filter(requestFilter -> requestFilter.isFilterApplicable(filterDto))
                .toList();

        List<RecommendationRequest> recommendationRequestsFiltered = recommendationRequestsAll.stream()
                .filter(recommendationRequest -> suitableFilters.stream()
                        .allMatch(suitableFilter -> suitableFilter.apply(recommendationRequest, filterDto)))
                .toList();

        return mapper.allToDTO(recommendationRequestsFiltered);
    }

    public RecommendationRequestDto getRequest(Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("The RecommendationRequest for this id-"
                        + id + " will not be found in the database"));
        return mapper.toDTO(recommendationRequest);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("The RecommendationRequest for this id-"
                        + id + " will not be found in the database"));

        RequestStatus status = recommendationRequest.getStatus();
        if (status != RequestStatus.PENDING) {
            throw new DataValidationException("the status of the RecommendationRequest by id-" + id + ", not pending");
        }
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());

        return mapper.toDTO(recommendationRequest);
    }
}
