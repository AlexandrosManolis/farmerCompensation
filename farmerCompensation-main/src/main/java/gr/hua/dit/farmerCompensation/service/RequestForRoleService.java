package gr.hua.dit.farmerCompensation.service;

import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.RequestForRole;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.repository.RequestForRoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RequestForRoleService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RequestForRoleRepository requestForRoleRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void saveRequest(RequestForRole requestForRole, Integer user_id) {

        User user = userDAO.getUserProfile(user_id);
        requestForRole.setUser(user);

        requestForRoleRepository.save(requestForRole);
    }

    public List<RequestForRole> getPendingRoleRequests() {
        return requestForRoleRepository.findAll();
    }

    @Transactional
    public Integer updateRequest(RequestForRole requestForRole) {
        requestForRole = requestForRoleRepository.save(requestForRole);
        return requestForRole.getId();
    }

    public List<User> getUsersWithRoleRequests() {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT DISTINCT r.user FROM RequestForRole r WHERE r.role_status = 'Pending' AND r.user IS NOT NULL",
                User.class
        );

        return query.getResultList();
    }
}
