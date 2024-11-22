package school.faang.user_service.repository;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Type;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Country;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long> {

    @Override
    @OneToMany(fetch = FetchType.EAGER)
    Iterable<Country> findAll();
}