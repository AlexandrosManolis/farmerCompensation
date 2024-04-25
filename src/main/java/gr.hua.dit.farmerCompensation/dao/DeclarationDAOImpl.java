package gr.hua.dit.farmerCompensation.dao;

import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class DeclarationDAOImpl implements DeclarationDAO{

    @PersistenceContext
    private EntityManager entityManager;

    //get all declarations for a user id
    @Override
    @Transactional
    public List<DeclarationForm> getDeclarations(Integer user_id) {
        //using typedQuery
        TypedQuery<DeclarationForm> query = entityManager.createQuery(
                "FROM DeclarationForm d WHERE d.user.id = :user_id", DeclarationForm.class);
        query.setParameter("user_id", user_id);
        return query.getResultList();
    }

    //get declaration with a specific id
    @Override
    public DeclarationForm getDeclaration(Integer declaration_id) {
        return entityManager.find(DeclarationForm.class, declaration_id);
    }

    //get user id with declaration id
    @Override
    public Integer getUserIdForDeclaration(Integer declaration_id) {
        DeclarationForm declaration = entityManager.find(DeclarationForm.class, declaration_id);
        if (declaration != null && declaration.getUser() != null) {
            return declaration.getUser().getId();
        } else {
            return null; // Handle the case where the declaration or user is not found
        }
    }

    //save declaration
    @Override
    @Transactional
    public DeclarationForm saveDeclaration(DeclarationForm declarationForm) {
        System.out.println("Declaration "+ declarationForm.getId());
        if (declarationForm.getId() == null) {
            entityManager.persist(declarationForm);
        } else {
            entityManager.merge(declarationForm);
        }
        return declarationForm;
    }
}
