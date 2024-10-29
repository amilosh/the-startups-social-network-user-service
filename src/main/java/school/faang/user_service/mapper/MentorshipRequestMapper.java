package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {
    Logger log = LoggerFactory.getLogger(MentorshipRequestMapper.class);

    @Mapping(source = "requester.id", target = "requesterUserId")
    @Mapping(source = "receiver.id", target = "receiverUserId")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss.SSS")
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss.SSS")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);


    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(source = "status", target = "status", qualifiedByName = "stringToStatus")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = dateFormat.parse(dateTimeStr);
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (ParseException e) {
            log.error("Error parsing date: ", e);
            throw new IllegalArgumentException(e);
        }
    }
}
