package school.faang.user_service.service;

import feign.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;

    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        User requester = userRepository.findById(dto.getRequesterId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователя, запрашивающего рекомендацию не существует"));
        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователя, получающего рекомендацию не существует"));

        Optional<RecommendationRequest> lastRequest = recommendationRequestRepository.findLatestPendingRequest(
                dto.getRequesterId(), dto.getReceiverId());

        if (lastRequest.isPresent()) {
            LocalDateTime lastRequestDate = lastRequest.get().getCreatedAt();
            if (lastRequestDate.isAfter(LocalDateTime.now().minusMonths(6))) {
                throw new IllegalArgumentException("Запрос этому пользователю можно отправлять только раз в полгода");
            }
        }

        List<Long> skillsIds = dto.getSkills();
        if (skillsIds != null && !skillsIds.isEmpty()) {
            long existingSkillsCount = skillRepository.countExisting(skillsIds);
            if (existingSkillsCount != skillsIds.size()) {
                throw new IllegalArgumentException("Некоторых скиллов нет в базе данных");
            }
        }

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(dto);

        if (recommendationRequest.getSkills() == null) {
            recommendationRequest.setSkills(new ArrayList<>());
        }

        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);

        recommendationRequest.setStatus(RequestStatus.PENDING);

        RecommendationRequest savedRequest = recommendationRequestRepository.save(recommendationRequest);

        List<Long> skillIds = dto.getSkills();
        if (skillIds != null && !skillIds.isEmpty()) {
            for (Long skillId : skillIds) {
                Skill skill = skillRepository.findById(skillId)
                        .orElseThrow(() -> new IllegalArgumentException("В базе данных нет скилла с id: " + skillId));

                SkillRequest skillRequest = new SkillRequest();
                skillRequest.setSkill(skill);
                skillRequest.setRequest(savedRequest);

                skillRequestRepository.save(skillRequest);

                savedRequest.getSkills().add(skillRequest);
            }
        }

        return recommendationRequestMapper.toDto(savedRequest);
    }


    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        List<RecommendationRequest> requests = recommendationRequestRepository.findAll();

        return requests.stream()
                .filter(request -> filter.getStatus() == null || request.getStatus() == filter.getStatus())
                .filter(request -> filter.getRequesterId() == null || request.getRequester()
                        .getId().equals(filter.getRequesterId()))
                .filter(request -> filter.getReceiverId() == null || request.getReceiver()
                        .getId().equals(filter.getReceiverId()))
                .filter(request -> filter.getStartDate() == null || !request.getCreatedAt()
                        .isBefore(filter.getStartDate()))
                .filter(request -> filter.getEndDate() == null || !request.getCreatedAt()
                        .isAfter(filter.getEndDate()))
                .map(recommendationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecommendationRequestDto getRequest(Long id) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.RecommendationRequestNotFoundException(
                        "Запрос на рекомендацию с таким id не найден"));
        return recommendationRequestMapper.toDto(request);
    }

    public RecommendationRequestDto rejectRequest(Long id, RejectionDto rejection) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.RecommendationRequestNotFoundException(
                        "Запрос на рекомендацию с таким id не найден"));

        if (request.getStatus() == RequestStatus.ACCEPTED || request.getStatus() == RequestStatus.REJECTED) {
            throw new IllegalStateException(
                    "Невозможно отклонить запрос на рекомендацию, поскольку он уже имеет статус " + request.getStatus());
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());

        recommendationRequestRepository.save(request);

        return recommendationRequestMapper.toDto(request);
    }
}
