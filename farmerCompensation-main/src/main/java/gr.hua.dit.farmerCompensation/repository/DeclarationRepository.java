package gr.hua.dit.farmerCompensation.repository;

import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeclarationRepository extends JpaRepository<DeclarationForm, Integer> {

    //check if user with an id exists
    boolean existsByUserId(Long userId);

    //find by field address
    Optional<DeclarationForm> findByFieldAddress(String fieldAddress);

}
