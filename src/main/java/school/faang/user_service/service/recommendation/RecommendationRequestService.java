package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRepository skillRepository;
    private final List<RecommendationRequestFilter> filters;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto dto) {

        checkUserById(dto.getRequesterId(), "Requester not found");
        checkUserById(dto.getReceiverId(), "Receiver not found");
        validatePendingRequest(dto);
        validateSkillsExistence(dto.getSkills());

        RecommendationRequest entity = recommendationRequestMapper.toEntity(dto);
        entity.setStatus(RequestStatus.valueOf("PENDING"));
        RecommendationRequest savedRequest = recommendationRequestRepository.save(entity);

        dto.getSkills().forEach(skillId -> {
            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setRequest(savedRequest);
            skillRequest.setSkill(skillRepository.getReferenceById(skillId));
            savedRequest.addSkillRequest(skillRequest);
        });

        recommendationRequestRepository.save(savedRequest);
        return recommendationRequestMapper.toDto(savedRequest);
    }

    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto filter) {
        List<RecommendationRequest> allRequests = recommendationRequestRepository.findAll();
        return filters.stream()
                .filter(f -> f.isApplicable(filter))
                .flatMap(f -> f.apply(allRequests.stream(), filter))
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRequest(Long id) {
        return recommendationRequestRepository.findById(id)
                .map(recommendationRequestMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request not found"));
    }

    @Transactional
    public RecommendationRequestDto rejectRequest(Long id, RejectionDto rejection) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request not found"));

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new IllegalArgumentException("Recommendation request is not pending");
        }

        request.setStatus(RequestStatus.valueOf("REJECTED"));
        request.setRejectionReason(rejection.reason());

        recommendationRequestRepository.save(request);

        return recommendationRequestMapper.toDto(request);
    }

    private void checkUserById(Long userId, String errorMessage) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validatePendingRequest(RecommendationRequestDto dto) {
        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository.findLatestPendingRequest(
                dto.getRequesterId(), dto.getReceiverId());
        if (latestPendingRequest.isPresent() &&
                latestPendingRequest.get()
                        .getCreatedAt()
                        .isAfter(LocalDateTime.now().minusMonths(6))) {

            throw new IllegalArgumentException("A recommendation request can only be sent once every 6 months.");
        }
    }

    private void validateSkillsExistence(List<Long> skillIds) {
        long existingSkillsCount = skillRepository.countExisting(skillIds);
        if (existingSkillsCount != skillIds.size()) {
            throw new IllegalArgumentException("One or more skills do not exist.");
        }
    }
}