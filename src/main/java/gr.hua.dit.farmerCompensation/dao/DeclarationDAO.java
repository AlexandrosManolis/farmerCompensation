package gr.hua.dit.farmerCompensation.dao;

import gr.hua.dit.farmerCompensation.entity.DeclarationForm;

import java.util.List;

public interface DeclarationDAO {

    //get declarations for a user id
    public List<DeclarationForm> getDeclarations(Integer user_id);

    //get a declaration with a specific id
    public DeclarationForm getDeclaration(Integer declaration_id);

    //save declaration
    public DeclarationForm saveDeclaration(DeclarationForm declarationForm);

    //get user id for a specific declaration
    public Integer getUserIdForDeclaration(Integer declaration_id);
}
