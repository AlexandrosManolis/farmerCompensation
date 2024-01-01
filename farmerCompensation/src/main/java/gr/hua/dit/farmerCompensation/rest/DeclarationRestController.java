package gr.hua.dit.farmerCompensation.rest;

import gr.hua.dit.farmerCompensation.dao.DeclarationDAO;
import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.service.DeclarationService;
import gr.hua.dit.farmerCompensation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
//@RequestMapping("/api/declaration")
public class DeclarationRestController {

    @Autowired
    private DeclarationDAO declarationDAO;

    @Autowired
    private DeclarationService declarationService;

    @Autowired
    private UserService userService;



/*
    @GetMapping("/{declaration_id}/userProfile")
    public User getUserProfileForDeclaration(@PathVariable Integer declaration_id) {
        Integer user_id = declarationService.getUserIdForDeclaration(declaration_id);
        if (user_id != null) {
            return userService.getUserProfile(user_id);
        } else {
            // Handle the case where the user ID is not found for the declaration
            return null;
        }
    }

    @GetMapping("/{user_id}")
    public List<DeclarationForm> getDeclaration(@PathVariable Integer user_id) {
        List<DeclarationForm> dec = declarationDAO.getDeclarations(user_id);
        return dec;
    }*/
/*
    @GetMapping("/save")
    public DeclarationForm saveDeclaration(@RequestBody DeclarationForm declarationForm, Integer user_id){
        return declarationDAO.saveDeclaration(declarationForm, user_id);
    }

*/


}
