package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.File;


@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    File findByUserId(Long userId);
}
