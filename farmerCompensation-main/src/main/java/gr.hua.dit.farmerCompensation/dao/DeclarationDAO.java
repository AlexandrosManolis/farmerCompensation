package gr.hua.dit.farmerCompensation.dao;

import gr.hua.dit.farmerCompensation.entity.DeclarationForm;

import java.util.List;

public interface DeclarationDAO {
    public List<DeclarationForm> getDeclarations(Integer user_id);

    public DeclarationForm getDeclaration(Integer declaration_id);

    public DeclarationForm saveDeclaration(DeclarationForm declarationForm);

    public Integer getUserIdForDeclaration(Integer declaration_id);
}
