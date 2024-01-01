package gr.hua.dit.farmerCompensation.dao;

import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO{


    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public User getUserProfile(Integer user_id) {
        return entityManager.find(User.class, user_id);
    }

    @Override
    public Optional<Long> getUserId(String username) {
        try {
            Long userId = entityManager.createQuery(
                            "SELECT u.id FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", username)
                    .getSingleResult();

            return Optional.of(userId);
        } catch (NoResultException | NonUniqueResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        System.out.println("student "+ user.getId());
        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            entityManager.merge(user);
        }
    }

}
