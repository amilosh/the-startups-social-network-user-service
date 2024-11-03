package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.RejectionRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recRequestFilters;
    private final RejectionRequestMapper rejectionRequestMapper;
    private SkillRepository skillRepository;
    private SkillRequestRepository skillRequestRepository;

    @Autowired
    public RecommendationRequestService(RecommendationRequestRepository recommendationRequestRepository,
                                        RecommendationRequestMapper recommendationRequestMapper,
                                        List<RecommendationRequestFilter> recRequestFilters, RejectionRequestMapper rejectionRequestMapper) {
        this.recommendationRequestRepository = recommendationRequestRepository;
        this.recommendationRequestMapper = recommendationRequestMapper;
        this.recRequestFilters = recRequestFilters;
        this.rejectionRequestMapper = rejectionRequestMapper;
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

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        Optional<RecommendationRequest> requesterExists = recommendationRequestRepository.findById(recommendationRequest.getRequesterId());
        Optional<RecommendationRequest> receiverExists = recommendationRequestRepository.findById(recommendationRequest.getReceiverId());
        List<Skill> skills = skillRepository.findAllById(recommendationRequest.getSkillIds());
        RecommendationRequest recommendationRequestEntity = null;

        if (requesterExists.isPresent()
                && receiverExists.isPresent()
                && canSendRecommendationRequest(recommendationRequest.getRequesterId(),
                   recommendationRequest.getReceiverId())
                && skillExists(skills, recommendationRequest)) {
            recommendationRequestEntity = recommendationRequestMapper.toEntity(recommendationRequest);
            recommendationRequestRepository.save(recommendationRequestEntity);
        } else {
            log.info("Requester or Receiver not found");
        }
        return recommendationRequestMapper.toDto(recommendationRequestEntity);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filters) {
        Stream<RecommendationRequest> requests = recommendationRequestRepository.findAll().stream();
        recRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(requests, filters));
        return requests
                .map(recommendationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecommendationRequestDto getRequest(RequestFilterDto filters) {
        return recommendationRequestRepository.findById(filters.getId())
                .map(recommendationRequestMapper::toDto)
                .orElseThrow(() -> {
                    log.info("Request with ID {} not found", filters.getId());
                    return new EntityNotFoundException("Request not found for ID: " + filters.getId());
                });

    }

    public RejectionDto rejectRequest(long id, RejectionDto rejectionRequest) {
        RecommendationRequest recommendationRequest = null;
        if (rejectionRequest.getStatus().equals("PENDING")) {
            RecommendationRequest request = rejectionRequestMapper.toEntity(rejectionRequest);
            request.setRejectionReason(rejectionRequest.getReason());
            recommendationRequest = recommendationRequestRepository.save(request);
        }
        return rejectionRequestMapper.toDto(recommendationRequest);
    }

    private boolean skillExists(List<Skill> skills, RecommendationRequestDto recommendationRequest) {
        return skills.size() == recommendationRequest.getSkillIds().size();
    }

    public boolean isMessageEmpty(RecommendationRequestDto recommendationRequest) {
        return recommendationRequest.getMessage().isEmpty();
    }
}
