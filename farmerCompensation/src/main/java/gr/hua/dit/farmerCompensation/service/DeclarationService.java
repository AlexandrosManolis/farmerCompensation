package gr.hua.dit.farmerCompensation.service;

import gr.hua.dit.farmerCompensation.dao.DeclarationDAO;
import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.repository.DeclarationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeclarationService {

    @Autowired
    private DeclarationRepository declarationRepository;
    @Autowired
    private DeclarationDAO declarationDAO;

    @Autowired
    private UserDAO userDAO;

    @Transactional
    public void saveDeclaration(DeclarationForm declarationForm, Integer user_id) {

        User user = userDAO.getUserProfile(user_id);
        declarationForm.setUser(user);
        declarationRepository.save(declarationForm);
    }
    @Transactional
    public Integer getUserIdForDeclaration(Integer declarationId) {
        return declarationDAO.getUserIdForDeclaration(declarationId);
    }

    @Transactional
    public DeclarationForm getDeclaration(int user_id){return declarationRepository.findById(user_id).get();}

    public Optional<Long> getUserDeclaration(String username){
        Optional<Long> user = userDAO.getUserId(username);
        return user;
    }

    @Transactional
    public Object getDeclarations() {
        return declarationRepository.findAll();
    }

    public void deleteDeclaration(int declaration_id){
        declarationRepository.deleteById(declaration_id);
    }

}
