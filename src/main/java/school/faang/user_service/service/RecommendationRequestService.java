package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestService skillRequestService;
    private final UserService userService;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final List<Filter<RecommendationRequest, RequestFilterDto>> filters;

    @Autowired
    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        User requester = userService.getUserById(dto.getRequesterId());
        User receiver = userService.getUserById(dto.getReceiverId());

        recommendationRequestValidator.validateUsersExistence(requester, receiver);
        recommendationRequestValidator.validateRequestFrequency(dto.getRequesterId(), dto.getReceiverId());
        recommendationRequestValidator.validateSkillsExistence(dto.getSkills());

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(dto);

        if (recommendationRequest.getSkills() == null) {
            recommendationRequest.setSkills(new ArrayList<>());
        }

        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);
        recommendationRequest.setStatus(RequestStatus.PENDING);

        RecommendationRequest savedRequest = recommendationRequestRepository.save(recommendationRequest);

        if (dto.getSkills() != null && !dto.getSkills().isEmpty()) {
            List<Skill> skills = skillRequestService.getSkillsByIds(dto.getSkills());
            for (Skill skill : skills) {
                SkillRequest skillRequest = skillRequestService.createSkillRequest(skill, savedRequest);
                savedRequest.getSkills().add(skillRequest);
            }
        }

        return recommendationRequestMapper.toDto(savedRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        Stream<RecommendationRequest> stream = recommendationRequestRepository.findAll().stream();

        for (Filter<RecommendationRequest, RequestFilterDto> filter : filters) {
            if (filter.isApplicable(filterDto)) {
                stream = filter.apply(stream, filterDto);
            }
        }

        return stream
                .map(recommendationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecommendationRequestDto getRequest(Long id) {
        RecommendationRequest request = recommendationRequestValidator.getAndValidateRecommendationRequest(id);

        return recommendationRequestMapper.toDto(request);
    }

    @Transactional
    public RecommendationRequestDto rejectRequest(Long id, RejectionDto rejection) {
        RecommendationRequest request = recommendationRequestValidator.getAndValidateRecommendationRequest(id);

        recommendationRequestValidator.validateRejectRequest(request);

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());

        recommendationRequestRepository.save(request);

        return recommendationRequestMapper.toDto(request);
    }
}
