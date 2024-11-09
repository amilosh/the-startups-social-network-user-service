package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestServiceValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestServiceValidator validator;
    private final RecommendationRequestMapper mapper;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;


    public void create(RecommendationRequestDto recommendationRequestDto) {
        validator.validateExistsRequesterAndReceiverInDatabase(recommendationRequestDto);
        validator.validateSixMonthRequestLimit(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId());
        validator.validateExistsSkillsInDatabase(recommendationRequestDto.getSkillIds());

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
        entitySkillRequests.forEach(entityRecommendationRequest::addSkillRequest);

        recommendationRequestRepository.save(entityRecommendationRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter){
        List<RecommendationRequest> recommendationRequestsAll = recommendationRequestRepository.findAll();

        return null;
    }
}
