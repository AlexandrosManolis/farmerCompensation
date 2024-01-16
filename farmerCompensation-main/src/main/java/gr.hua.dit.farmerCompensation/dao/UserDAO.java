package gr.hua.dit.farmerCompensation.dao;

import gr.hua.dit.farmerCompensation.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO {
    public User getUserProfile(Integer user_id);

    Integer getUserId(String username);

    public void saveUser(User user);
}
