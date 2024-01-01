package gr.hua.dit.farmerCompensation.dao;

import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO {
    public User getUserProfile(Integer user_id);

    Optional<Long> getUserId(String username);

    public void saveUser(User user);
}
