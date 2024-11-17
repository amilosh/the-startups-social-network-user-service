package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRepository skillRepository;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto dto) {

        // Проверки
        //todo какие рода проверки правильно делать в Controller / Service ?
        checkUserById(dto.getRequesterId(), "Requester not found");
        checkUserById(dto.getReceiverId(), "Receiver not found");
        validatePendingRequest(dto);
        validateSkillsExistence(dto.getSkills());

        // Создаем RecommendationRequest
        RecommendationRequest entity = recommendationRequestMapper.toEntity(dto);
        //скорее тут нужно определять статус. Именно на create он должен быть PENDING, в запросе его вообще не должно быть, и там его в ignored
        entity.setStatus(RequestStatus.valueOf("PENDING"));
        RecommendationRequest savedRequest = recommendationRequestRepository.save(entity);

        // Создаем и связываем SkillRequest c RecommendationRequest
        /*todo: В задании сказано, что SkillRequest создаются в Service после создания RecommendationRequest через метод create skillRequestRepository.
            - зачем создавать в репозитории метод create, если можно использовать save?
            - зачем вообще использовать репозиторий skillRequestRepository?
                Можно создать SkillRequest через new и добавить в savedRequest.addSkillRequest и они создадутся в БД по CascadeType.ALL.
            - видится что вообще лучше сначала создать RecommendationRequest с skillRequests и сделать только один recommendationRequestRepository.save(entity)
                без обращения к skillRequestRepository, а в skill_request создались бы записи по CascadeType.ALL при сохранении RecommendationRequest.
                Но для skillRequest нужно указывать request_id, потому сначала создавать savedRequest, после делать UPDATE savedRequest с добавлением skillRequests.
        */

        /*todo: Приходится создавать пустой список, так как в RecommendationRequest не инициализируется список skills
           и при попытке добавить в него SkillRequest будет NullPointerException.
           Как было бы идеально?
        */
        savedRequest.setSkills(new ArrayList<>());

        dto.getSkills().forEach(skillId -> {
            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setRequest(savedRequest);
            skillRequest.setSkill(skillRepository.getReferenceById(skillId));
            // Видимо так произойдет связка по ключам в БД по СascadeType.ALL.
            // В момент завершения метода с @Transactional всегда происходит commit на измененную entity и после INSERT RecommendationRequest еще будет UPDATE?
            savedRequest.addSkillRequest(skillRequest);
        });


//        dto.getSkills().forEach(skillId -> {
//            SkillRequest skillRequest = skillRequestRepository.create(savedRequest.getId(), skillId);
//            savedRequest.addSkillRequest(skillRequest); //видимо так произойдет связка по ключам в БД по СascadeType.ALL
//        });

        return recommendationRequestMapper.toDto(savedRequest);
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