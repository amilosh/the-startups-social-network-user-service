package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserValidator userValidator;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;
    private final RecommendationService recommendationService;
    private final SkillRepository skillRepository;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        log.info("Creating a recommendations request from user with id {} for user with id {}",
                recommendationRequest.getReceiverId(), recommendationRequest.getRequesterId());

        userValidator.validateUser(recommendationRequest.getRequesterId());
        userValidator.validateUser(recommendationRequest.getReceiverId());
        recommendationRequestValidator.validateRecommendation(recommendationRequest);

        RecommendationRequest request = recommendationRequestMapper.toEntity(recommendationRequest);

        RecommendationRequest finalRequest = request;
        List<SkillRequest> skillRequests = recommendationRequest.getSkillRequests().stream()
                .map(skillRequestDto -> {
                    Skill skill = getSkill(skillRequestDto.getSkillId());
                    return SkillRequest.builder()
                            .request(finalRequest)
                            .skill(skill)
                            .build();
                }).toList();

        request.setSkills(skillRequests);

        request = recommendationRequestRepository.save(request);
        log.info("Recommendation request with id {} successfully saved", request.getId());

        return recommendationRequestMapper.toDto(request);
    }

    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto requestFilter) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();
        recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilter))
                .forEach(filter -> filter.apply(recommendationRequests, requestFilter));
        log.info("Getting a list of recommendation requests after filtering");
        return recommendationRequestMapper.toDtoList(recommendationRequests.toList());
    }

    public RecommendationRequestDto getRequest(Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestValidator.validateRecommendationFromBd(id);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public RecommendationRequestDto rejectRequest(Long id, RejectionDto rejectionDto) {
        RecommendationRequest recommendationRequest = recommendationRequestValidator.validateRecommendationFromBd(id);
        recommendationRequestValidator.checkRequestsStatus(id, recommendationRequest.getStatus());
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getRejectionReason());
        recommendationRequestRepository.save(recommendationRequest);

        log.info("Recommendation request with id {} was rejected", id);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    @Transactional
    public ResponseRecommendationDto acceptRequest(long id) {
        RecommendationRequest recommendationRequest = recommendationRequestValidator.validateRecommendationFromBd(id);
        recommendationRequestValidator.checkRequestsStatus(id, recommendationRequest.getStatus());
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);
        recommendationRequestRepository.save(recommendationRequest);
        log.info("Recommendation request with id {} was accepted", id);

        return recommendationService.createRecommendationAfterRequestAccepting(recommendationRequest);
    }

    private Skill getSkill(Long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    log.warn("Skill with id {} not found", skillId);
                    return new NoSuchElementException(
                            String.format("There is no skill with id = %d", skillId));
                });
    }
}
