package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final RecommendationRequestRepository repository;
    private final SkillRequestRepository skillRequestRepository;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        LocalDateTime localDateTime = LocalDateTime.now();

        recommendationRequestValidator.validateRequesterAndReceiver(recommendationRequestDto);
        recommendationRequestValidator.validateRequestAndCheckTimeLimit(recommendationRequestDto, localDateTime);

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);

        recommendationRequest.getSkills().stream()
                .filter(skill -> !skillRequestRepository.existsById(skill.getId()))
                .forEach(skill -> skillRequestRepository.create(recommendationRequestDto.getId(), skill.getId()));

        RecommendationRequest createRequest = repository.save(recommendationRequest);

        return recommendationRequestMapper.toDto(createRequest);
    }

    @Transactional
    public RecommendationRequestDto getRecommendationRequest(long id) {
        RecommendationRequest request = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found RequestRecommendation for id: " + id));
        return recommendationRequestMapper.toDto(request);
    }

    @Transactional
    public RecommendationRequestDto rejectRecommendationRequest(Long id, String reason) throws DataValidationException {
        RecommendationRequestDto recommendationRequestDto = getRecommendationRequest(id);
        if (recommendationRequestDto.getStatus() == RequestStatus.PENDING) {
            RecommendationRequest request = recommendationRequestMapper.toEntity(recommendationRequestDto);
            request.setStatus(RequestStatus.REJECTED);
            request.setRejectionReason(reason);
            return recommendationRequestMapper.toDto(request);
        } else {
            throw new DataValidationException("It is impossible to refuse a request that is not in a pending state");
        }
    }

    @Transactional
    public List<RecommendationRequestDto> getFilteredRecommendationRequest(Long receiverId, RecommendationRequestFilterDto filters) {
        return null;
    }

}
