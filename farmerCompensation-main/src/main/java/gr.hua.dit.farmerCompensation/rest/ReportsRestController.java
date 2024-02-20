package gr.hua.dit.farmerCompensation.rest;

import gr.hua.dit.farmerCompensation.dao.DeclarationDAO;
import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.service.DeclarationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/declaration/report/{user_id}")
public class ReportsRestController {

    @Autowired
    private DeclarationDAO declarationDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private DeclarationService declarationService;

    //get declaration reports if exists
    @GetMapping("{declaration_id}")
    public ResponseEntity<?> declarationReport(@PathVariable int user_id, @PathVariable int declaration_id){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);
        User user = userDAO.getUserProfile(user_id);

        if (declarationForm == null) {
            return ResponseEntity.badRequest().body("No Declaration found!");
        }else{
            return new ResponseEntity<>(declarationForm, HttpStatus.OK);
        }
    }

    //using to change status to check on site after call this method
    @PostMapping("checkonsite/{declaration_id}")
    public ResponseEntity<?> ChangeToCheck(@PathVariable int user_id, @PathVariable int declaration_id){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);
        User user = userDAO.getUserProfile(user_id);

        if (declarationForm == null) {
            return ResponseEntity.badRequest().body("Not Declaration found!");
        }else{
            declarationForm.setStatus("Check on site");
            declarationService.saveDeclaration(declarationForm,user_id);
            return ResponseEntity.ok(Map.of("message", "Status changed to Check on site!"));
        }
    }

    //using to change status to accepted after call this method and set amount of compensation
    @GetMapping("acceptReport/{declaration_id}")
    public ResponseEntity<?> AcceptReport(@PathVariable int user_id, @PathVariable int declaration_id, @RequestParam String amount, @RequestBody DeclarationForm declarationForm){
        DeclarationForm acceptedDeclarationForm = declarationDAO.getDeclaration(declaration_id);
        acceptedDeclarationForm.setStatus("Accepted");
        acceptedDeclarationForm.setAmount(declarationForm.getAmount());

        declarationService.updateDeclaration(acceptedDeclarationForm);


        return ResponseEntity.ok("Declaration set to accepted!");
    }

    //using to change status to accepted after call this method and set amount of compensation
    @PostMapping("acceptReport/{declaration_id}")
    public ResponseEntity<?> acceptReport(@PathVariable int user_id, @PathVariable int declaration_id, @RequestParam String amount, Model model){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);
        User user = userDAO.getUserProfile(user_id);

        declarationForm.setStatus("Accepted");
        declarationForm.setAmount(amount);

        declarationService.saveDeclaration(declarationForm,user_id);
        return new ResponseEntity<>(declarationForm,HttpStatus.OK);
    }

    //using to change status to rejected after call this method
    @PostMapping("rejectReport/{declaration_id}")
    public ResponseEntity<?> rejectReport(@PathVariable int user_id, @PathVariable int declaration_id, Model model){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);

        User user = userDAO.getUserProfile(user_id);
        declarationForm.setStatus("Rejected");

        declarationService.saveDeclaration(declarationForm,user_id);
        return new ResponseEntity<>(declarationForm,HttpStatus.OK);
    }

}
