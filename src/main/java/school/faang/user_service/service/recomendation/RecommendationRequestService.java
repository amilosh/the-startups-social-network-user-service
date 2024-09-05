package school.faang.user_service.service.recomendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
//@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;

    @Autowired
    public RecommendationRequestService(RecommendationRequestRepository recommendationRequestRepository, UserRepository userRepository, RecommendationRequestMapper mapper, SkillRequestRepository skillRequestRepository, SkillRepository skillRepository) {
        this.recommendationRequestRepository = recommendationRequestRepository;
        this.userRepository = userRepository;
        this.recommendationRequestMapper = mapper;
        this.skillRequestRepository = skillRequestRepository;
        this.skillRepository = skillRepository;
    }


    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        if (!userRepository.existsById(recommendationRequestDto.getRequesterId()) || !userRepository.existsById(recommendationRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("Requester id or receiver id is wrong");
        }
//        if (!isSkillsInDb(recommendationRequestDto)) {
//            throw new NoSuchElementException("No such skills in database");
//        }
        System.out.println("Next");
        RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.mapToEntity(recommendationRequestDto);
        List<SkillRequest> skillRequests = StreamSupport.stream(skillRequestRepository.findAllById(recommendationRequestDto.getSkillsId()).spliterator(), false).toList();
        recommendationRequestEntity.setSkills(skillRequests);
        recommendationRequestEntity = recommendationRequestRepository.save(recommendationRequestEntity);
        recommendationRequestEntity.getSkills()
                .forEach(skillRequest -> skillRequestRepository.create(recommendationRequestDto.getRequesterId(), skillRequest.getId()));
        return recommendationRequestMapper.mapToDto(recommendationRequestEntity);
    }

    public boolean isSkillsInDb(RecommendationRequestDto recommendationRequestDto) {
        List<SkillRequest> skillRequests = (List<SkillRequest>) skillRequestRepository.findAllById(recommendationRequestDto.getSkillsId());
        List<Skill> skills = skillRequests.stream()
                .map(SkillRequest::getSkill)
                .toList();
        return skillRepository.existsAllByIdIn(skills);
    }

}

//    public boolean checkUsers(RecommendationRequestDto recommendationRequestDto) {
//        List<Long> userIds = List.of(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId());
//        System.out.println(recommendationRequestDto.getRequesterId());
//        System.out.println(recommendationRequestDto.getReceiverId());
//
//        return userRepository.existsAllByIdIn(userIds);
//    }

//public boolean requestAllowed(RecommendationRequestDto recommendationRequestDto) {
//    long count = recommendationRequestRepository.findAll().stream()
//            .filter(request -> request.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(6)))
//            .filter(request -> request.getRequester().getId().equals(recommendationRequestDto.getRequesterId()))
//            .filter(request -> request.getReceiver().getId().equals(recommendationRequestDto.getReceiverId()))
//            .count();
//    return count == 0;
//}




