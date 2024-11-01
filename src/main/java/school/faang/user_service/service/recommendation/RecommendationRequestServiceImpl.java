package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestRejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    private final UserRepository userRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;

    private final RecommendationRequestMapper recommendationRequestMapper;

    @Override
    public RecommendationRequestDto create(RecommendationRequestDto request) {
        validateRecommendationRequest(request);

        request.getSkillIds().forEach(skillId -> skillRequestRepository.create(request.getId(), skillId));

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(request);
        recommendationRequest.setRequester(userRepository.findById(request.getRequesterId()).orElseThrow(() -> new IllegalArgumentException("Requester id %s not exist".formatted(request.getRequesterId()))));
        recommendationRequest.setReceiver(userRepository.findById(request.getReceiverId()).orElseThrow(() -> new IllegalArgumentException("Receiver id %s not exist".formatted(request.getReceiverId()))));
        recommendationRequest.setSkills(skillRequestRepository.findByRequestId(recommendationRequest.getId()));

        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(recommendationRequest));
    }

    @Override
    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto filter) {
        if (filter == null) {
            return recommendationRequestMapper.toDto(recommendationRequestRepository.findAll());
        }
        return recommendationRequestMapper.toDto(recommendationRequestRepository.findAll())
                .stream()
                .filter(reqDto -> filter.getStatus() == null || reqDto.getStatus().equals(filter.getStatus()))
                .filter(reqDto -> filter.getRequesterId() == null || reqDto.getRequesterId() == filter.getRequesterId())
                .filter(reqDto -> filter.getReceiverId() == null || reqDto.getRequesterId() == filter.getReceiverId())
                .toList();
    }

    @Override
    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestMapper.toDto(recommendationRequestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Recommendation request id %s not found".formatted(id))));
    }

    @Override
    public RecommendationRequestDto rejectRequest(long id, RecommendationRequestRejectionDto rejection) {
        validateRejectionRequest(id);

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Recommendation request id %s not found".formatted(id)));
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setUpdatedAt(LocalDateTime.now());
        recommendationRequest.setRejectionReason(rejection.getReason());

        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(recommendationRequest));
    }

    private void validateRejectionRequest(long id) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository.findById(id);
        if (recommendationRequest.isPresent()) {
            if (recommendationRequest.get().getStatus().equals(RequestStatus.ACCEPTED)) {
                throw new IllegalArgumentException("The recommendation request id %s is already accepted".formatted(id));
            }
            if (recommendationRequest.get().getStatus().equals(RequestStatus.REJECTED)) {
                throw new IllegalArgumentException("The recommendation request id %s is already rejected".formatted(id));
            }
        }
    }

    private void validateRecommendationRequest(RecommendationRequestDto request) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository.findLatestPendingRequest(request.getRequesterId(), request.getReceiverId());
        if (recommendationRequest.isPresent()) {
            if (recommendationRequest.get().getCreatedAt().plusMonths(6L).isAfter(request.getCreatedAt())) {
                throw new IllegalArgumentException("A recommendation request from the same user to another can be sent no more than once every 6 months.");
            }
        }

        request.getSkillIds().forEach(skillId -> {
            if (skillRepository.findUserSkill(skillId, request.getReceiverId()).isEmpty()) {
                throw new IllegalArgumentException("The receiver user id %s does not have the skill %s".formatted(request.getReceiverId(), skillId));
            }

            if (!skillRepository.existsById(skillId)) {
                throw new IllegalArgumentException("Skill id %s not exist".formatted(skillId));
            }
        });
    }
}
