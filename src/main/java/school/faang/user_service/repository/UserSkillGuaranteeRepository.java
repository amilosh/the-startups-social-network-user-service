package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.UserSkillGuarantee;

@Repository
public interface UserSkillGuaranteeRepository extends JpaRepository<UserSkillGuarantee, Long> {
    @Query(nativeQuery = true, value = "SELECT COUNT(*) > 0 FROM user_skill_guarantee WHERE user_id = ?1 AND skill_id = ?2")
    boolean existsByUserIdAndSkillId(long userId, long skillId);

    @Query(nativeQuery = true, value = "INSERT INTO user_skill_guarantee (user_id, skill_id, guarantor_id) VALUES (?1, ?2, ?3) RETURNING id")
    Long create(long userId, long skillId, long guarantorId);

    @Query(nativeQuery = true, value = "SELECT COALESCE(MAX(guarantor_id), 0) FROM user_skill_guarantee")
    Long findMaxGuarantorId();
}