package gr.hua.dit.farmerCompensation.service;

import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.RequestForRole;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.repository.RequestForRoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestForRoleService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RequestForRoleRepository requestForRoleRepository;

    @Autowired
    private EntityManager entityManager;

    //save request for role
    @Transactional
    public void saveRequest(RequestForRole requestForRole, Integer user_id) {

        User user = userDAO.getUserProfile(user_id);
        requestForRole.setUser(user);

        requestForRoleRepository.save(requestForRole);
    }

    //get requests that are pending
    public List<RequestForRole> getPendingRoleRequests() {
        return requestForRoleRepository.findAll();
    }

    //get users with role requests using typedQuery
    public List<User> getUsersWithRoleRequests() {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT DISTINCT r.user FROM RequestForRole r WHERE r.status = 'Pending' AND r.user IS NOT NULL", User.class);
        return query.getResultList();
    }

    //get reports
    public RequestForRole getReport(Integer requests_id) {
        return entityManager.find(RequestForRole.class, requests_id);
    }

    //delete request with query
    @Transactional
    public void deleteRequest(Integer request_id){
        Query query = entityManager.createQuery(
                "DELETE FROM RequestForRole r WHERE r.id = :requestId AND (r.status = 'Approved' or r.status='Rejected') AND r.user IS NOT NULL"
        );
        query.setParameter("requestId", request_id).executeUpdate();

    }

    @Transactional
    public boolean isUserPending(Long userId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(r) FROM RequestForRole r WHERE r.status = 'Pending' AND r.user.id = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
        if (count>0) {
            return true;
        }
        return false;
    }
}
