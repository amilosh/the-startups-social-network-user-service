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
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
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
    private final SkillRequestRepository skillRequestRepository;

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
        // Скорее тут нужно определять статус. Именно на create он должен быть PENDING, в запросе его вообще не должно быть, и там его в ignored
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
              - Инициализировать список skills в RecommendationRequest: savedRequest.setSkills(new ArrayList<>())
              - Создавать RecommendationRequest через new не получится, его создает mapStruct, в нем не инициализируются списки.
              - Или создавать в savedRequest.addSkillRequest(skillRequest) проверку на null и инициализацию списка (сделал так)
        */

        dto.getSkills().forEach(skillId -> {
            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setRequest(savedRequest);
            skillRequest.setSkill(skillRepository.getReferenceById(skillId));
            // Видимо так произойдет связка по ключам в БД по CascadeType.ALL.
            // В момент завершения метода с @Transactional всегда происходит commit на измененную entity и после INSERT RecommendationRequest еще будет UPDATE?
            savedRequest.addSkillRequest(skillRequest);
        });

        /*todo: При использовании create (как сказано в задании) в SkillRequestRepository возникает ошибка:
        [http-nio-8080-exec-2] WARN  o.s.w.s.m.m.a.ExceptionHandlerExceptionResolver - Resolved [org.springframework.dao.InvalidDataAccessApiUsageException:
         Modifying queries can only use void or int/Integer as return type;
         Offending method: public abstract school.faang.user_service.entity.recommendation.SkillRequest
        school.faang.user_service.repository.recommendation.SkillRequestRepository.create(long,long)"

        Метод create реализован не корректно?

        dto.getSkills().forEach(skillId -> {
            SkillRequest skillRequest = skillRequestRepository.create(savedRequest.getId(), skillId);
            savedRequest.addSkillRequest(skillRequest);
        });
        */

        //Явное сохранение RecommendationRequest после добавления SkillRequest. Можно и не делать, но так понятнее.
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
        return recommendationRequestMapper.toDto(request);
    }
}