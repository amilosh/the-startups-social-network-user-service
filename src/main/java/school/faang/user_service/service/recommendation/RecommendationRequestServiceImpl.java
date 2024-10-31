package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

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
        recommendationRequest.setSkills(skillRequestRepository.findByRequestId(recommendationRequest.getId()));

        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(recommendationRequest));
    }

    @Override
    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        if (filter == null) {
            return recommendationRequestMapper.toDto(recommendationRequestRepository.findAll());
        }
        return recommendationRequestMapper.toDto(recommendationRequestRepository.findAll())
                .stream()
                .filter(reqDto -> filter.getStatus() == null || reqDto.getStatus().equals(filter.getStatus()))
                .filter(reqDto -> filter.getSkillIds() == null || reqDto.getSkillIds().containsAll(filter.getSkillIds()))
                .toList();
    }

    @Override
    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestMapper.toDto(recommendationRequestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Recommendation request not found")));
    }

    @Override
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        validateRejectionRequest(id);

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Recommendation request not found"));
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());

        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(recommendationRequest));
    }

    private void validateRejectionRequest(long id) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository.findById(id);
        if (recommendationRequest.isPresent()) {
            if (recommendationRequest.get().getStatus().equals(RequestStatus.ACCEPTED)) {
                throw new IllegalArgumentException("The recommendation request is already accepted");
            }
            if (recommendationRequest.get().getStatus().equals(RequestStatus.REJECTED)) {
                throw new IllegalArgumentException("The recommendation request is already rejected");
            }
        }
    }

    private void validateRecommendationRequest(RecommendationRequestDto request) {
        if (!userRepository.existsById(request.getRequesterId())) {
            throw new IllegalArgumentException("Requester not exist");
        }

        if (!userRepository.existsById(request.getReceiverId())) {
            throw new IllegalArgumentException("Receiver not exist");
        }

        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository.findLatestPendingRequest(request.getRequesterId(), request.getReceiverId());
        if (recommendationRequest.isPresent()) {
            if (recommendationRequest.get().getCreatedAt().plusMonths(6L).isAfter(request.getCreatedAt())) {
                throw new IllegalArgumentException("A recommendation request from the same user to another can be sent no more than once every 6 months.");
            }
        }

        request.getSkillIds().forEach(skillId -> {
            if (!skillRepository.existsById(skillId)) {
                throw new IllegalArgumentException("Skill id %s not exist".formatted(skillId));
            }
        });
    }
}
