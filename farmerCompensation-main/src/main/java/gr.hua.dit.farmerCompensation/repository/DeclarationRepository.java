package gr.hua.dit.farmerCompensation.repository;

import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeclarationRepository extends JpaRepository<DeclarationForm, Integer> {

}
