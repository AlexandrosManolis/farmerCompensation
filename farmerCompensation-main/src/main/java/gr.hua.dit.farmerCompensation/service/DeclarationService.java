package gr.hua.dit.farmerCompensation.service;

import gr.hua.dit.farmerCompensation.dao.DeclarationDAO;
import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.repository.DeclarationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeclarationService {

    @Autowired
    private DeclarationRepository declarationRepository;
    @Autowired
    private DeclarationDAO declarationDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private EntityManager entityManager;

    //save declaration
    @Transactional
    public void saveDeclaration(DeclarationForm declarationForm, Integer user_id) {

        User user = userDAO.getUserProfile(user_id);
        declarationForm.setUser(user);
        declarationRepository.save(declarationForm);
    }

    //get users having declarations via typedQuery
    public List<User> getUsersWithDeclarations() {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT DISTINCT r.user FROM DeclarationForm r WHERE (r.status = 'Pending' OR r.status = 'Check on site') AND r.user IS NOT NULL", User.class);
        return query.getResultList();
    }

    public boolean doesDeclarationExistForUser(Long user_id) {
        return declarationRepository.existsByUserId(user_id);
    }

    @Transactional
    public Integer getUserIdForDeclaration(Integer declarationId) {
        return declarationDAO.getUserIdForDeclaration(declarationId);
    }

    @Transactional
    public DeclarationForm getDeclaration(int user_id){return declarationRepository.findById(user_id).get();}

    public Integer getUserDeclaration(String username){
        Integer user = userDAO.getUserId(username);
        return user;
    }

    //update declaration
    @Transactional
    public Integer updateDeclaration(DeclarationForm declarationForm) {
        declarationForm = declarationRepository.save(declarationForm);
        return declarationForm.getId();
    }

    @Transactional
    public Object getDeclarations() {
        return declarationRepository.findAll();
    }

    //delete declaration
    public void deleteDeclaration(int declaration_id){
        declarationRepository.deleteById(declaration_id);
    }

}
