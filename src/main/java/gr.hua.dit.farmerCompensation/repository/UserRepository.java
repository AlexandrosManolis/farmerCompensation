package gr.hua.dit.farmerCompensation.repository;

import gr.hua.dit.farmerCompensation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    //find by username
    Optional<User> findByUsername(String username);

    //check if user with a username exists
    Boolean existsByUsername(String username);

    //check if user with an email exists
    Boolean existsByEmail(String email);

    //check if user with an afm exists
    boolean existsByAfm(String afm);

    //check if user with an identity exists
    boolean existsByIdentity(String identity);

}
