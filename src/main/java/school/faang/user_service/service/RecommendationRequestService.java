package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.recommendationRequestFilters.Filter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static final int ACCEPTABLE_MONTH_BETWEEN_REQUEST = 6;

    private final UserService userService;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillService skillService;
    private final SkillRequestService skillRequestService;
    private final List<Filter<RequestFilterDto, RecommendationRequest>> filters;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequest recommendationRequest = mapToFullRecommendationRequest(recommendationRequestDto);
        validateAbilityToSendRepeatRequest(recommendationRequest);

        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        saveSkillRequests(recommendationRequest);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public List<RecommendationRequest> getRequests(RequestFilterDto filterDto) {
        Stream<RecommendationRequest> recommendationRequestsStream = recommendationRequestRepository.findAll().stream();

        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(recommendationRequestsStream, (stream, filter) -> filter.apply(stream, filterDto),
                        ((subGenStream, stream) -> stream))
                .distinct()
                .toList();
    }

    public RecommendationRequest getRequest(long id) {
        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unable to find recommendation request with id " + id));
    }

    public RecommendationRequest rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = getRequest(id);

        if (recommendationRequest.getStatus().equals(RequestStatus.ACCEPTED) || recommendationRequest.getStatus().equals(RequestStatus.REJECTED)) {
            throw new DataValidationException("The recommendation request has already been accepted or rejected");
        }

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.reason());
        return recommendationRequestRepository.save(recommendationRequest);
    }

    private void validateAbilityToSendRepeatRequest(RecommendationRequest recommendationRequest) {
        long requesterId = recommendationRequest.getRequester().getId();
        long receiverId = recommendationRequest.getReceiver().getId();
        RecommendationRequest lastRecommendationRequest = recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId)
                .orElse(null);
        if (lastRecommendationRequest != null) {
            boolean isAcceptable = ChronoUnit.MONTHS.between(LocalDateTime.now(), lastRecommendationRequest.getCreatedAt()) >= ACCEPTABLE_MONTH_BETWEEN_REQUEST;
            if (!isAcceptable) {
                throw new DataValidationException("Less than 6 months since the last request");
            }
        }
    }

    private RecommendationRequest mapToFullRecommendationRequest(RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);

        User requester = userService.getUserById(recommendationRequestDto.requesterId());
        User receiver = userService.getUserById(recommendationRequestDto.receiverId());
        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);

        List<SkillRequest> skillRequests = recommendationRequest.getSkills();
        skillRequests.forEach(skillRequest -> recommendationRequestDto.skills().forEach(skillRequestDto -> {
            skillRequest.setRequest(recommendationRequest);
            skillRequest.setSkill(skillService.getSkillById(skillRequestDto.skillId()));
        }));

        return recommendationRequest;
    }

    private void saveSkillRequests(RecommendationRequest recommendationRequest) {
        List<SkillRequest> skillRequests = recommendationRequest.getSkills();
        skillRequests.forEach(skillRequestService::save);
    }
}
