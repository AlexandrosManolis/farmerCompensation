package gr.hua.dit.farmerCompensation.repository;

import gr.hua.dit.farmerCompensation.entity.RequestForRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestForRoleRepository extends JpaRepository<RequestForRole, Long> {


}
