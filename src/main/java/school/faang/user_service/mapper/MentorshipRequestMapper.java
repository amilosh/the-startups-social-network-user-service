package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {

    @Mapping(source = "requester.id", target = "requesterUserId")
    @Mapping(source = "receiver.id", target = "receiverUserId")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss.SSS")
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss.SSS")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);


    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(source = "status", target = "status", qualifiedByName = "stringToStatus")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "stringToLocalDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "stringToLocalDateTime")
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Named("statusToString")
    default String statusToString(RequestStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToStatus")
    default RequestStatus stringToStatus(String status) {
        return status != null ? RequestStatus.valueOf(status) : null;
    }

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String dateTimeStr) {
        return DateFormatter.stringToLocalDateTime(dateTimeStr);
    }
}
