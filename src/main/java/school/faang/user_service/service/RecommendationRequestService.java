package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RecommendationRequestValidationException;
import school.faang.user_service.filter.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.RejectionRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recRequestFilters;
    private final UserRepository userRepository;
    private final RejectionRequestMapper rejectionRequestMapper;
    private SkillRepository skillRepository;
    private SkillRequestRepository skillRequestRepository;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        User requester = userRepository.
                findById(recommendationRequest.getRequesterId())
                .orElseThrow(() -> {
                    log.error("A request has failed to get the requester user from DB with an ID: {}", recommendationRequest.getRequesterId());
                    throw new EntityNotFoundException("User (requester) not found in DB");
                });
        User receiver = userRepository.
                findById(recommendationRequest.getReceiverId())
                .orElseThrow(() -> {
                    log.info("A request has failed to get the requester user from DB with an ID: {}", recommendationRequest.getRequesterId());
                    throw new EntityNotFoundException("User (receiver) not found in DB");
                });
        List<Skill> skills = skillRepository.findAllById(recommendationRequest.getSkillIds());
        RecommendationRequest recommendationRequestEntity = null;

        if (canSendRecommendationRequest(recommendationRequest.getRequesterId(),
                   recommendationRequest.getReceiverId())
                && skillExists(skills, recommendationRequest)) {
            recommendationRequestEntity = recommendationRequestMapper.toEntity(recommendationRequest);
            recommendationRequestEntity.setStatus(RequestStatus.PENDING);
            recommendationRequestEntity.setRequester(requester);
            recommendationRequestEntity.setReceiver(receiver);
            recommendationRequestRepository.save(recommendationRequestEntity);
        } else {
            log.error("A request to create a new recommendation request entity has been failed because Requester or Receiver not found!");
            throw new RecommendationRequestValidationException("Your request did not meet the validation requirements!");
        }
        return recommendationRequestMapper.toDto(recommendationRequestEntity);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filters) {
        Stream<RecommendationRequest> requests = recommendationRequestRepository.findAll().stream();
        recRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(requests, filters));
        log.info("A request to get the list of Recommendation Requests has been successfully processed!");
        return requests
                .map(recommendationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecommendationRequestDto getRequest(RequestFilterDto filters) {
        return recommendationRequestRepository.findById(filters.getId())
                .map(recommendationRequestMapper::toDto)
                .orElseThrow(() -> {
                    log.info("A request has failed to get the recommendation request with an ID: {}", filters.getId());
                    return new EntityNotFoundException("Request not found for ID: " + filters.getId());
                });

    }

    public RejectionDto rejectRequest(long id, RejectionDto rejectionRequest) {
        RecommendationRequest recommendationRequest = null;
        if (rejectionRequest.getStatus().equals(RequestStatus.PENDING)) {
            RecommendationRequest request = rejectionRequestMapper.toEntity(rejectionRequest);
            request.setRejectionReason(rejectionRequest.getReason());
            request.setStatus(RequestStatus.REJECTED);
            recommendationRequest = recommendationRequestRepository.save(request);
            log.info("A request to reject the recommendation request with an ID: {} has been successfully processed!", id);
        }
        return rejectionRequestMapper.toDto(recommendationRequest);
    }

    public boolean canSendRecommendationRequest(Long requesterId, Long receiverId) {
        Optional<RecommendationRequest> pendingRecommendationRequest =  recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);
        if (pendingRecommendationRequest.isPresent()) {
            LocalDateTime dateSixMonthLater = pendingRecommendationRequest.get().getCreatedAt().plusMonths(6);
            if (LocalDateTime.now().isBefore(dateSixMonthLater)) {
                return false;
            }
        }
        return true;
    }

    private boolean skillExists(List<Skill> skills, RecommendationRequestDto recommendationRequest) {
        return skills.size() == recommendationRequest.getSkillIds().size();
    }

    public boolean isMessageEmpty(RecommendationRequestDto recommendationRequest) {
        return recommendationRequest.getMessage().isEmpty();
    }
}
