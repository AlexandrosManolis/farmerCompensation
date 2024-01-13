package gr.hua.dit.farmerCompensation.controller;

import gr.hua.dit.farmerCompensation.dao.DeclarationDAO;
import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.service.DeclarationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/declaration")
public class DeclarationController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private DeclarationService declarationService;

    @Autowired
    private DeclarationDAO declarationDAO;

    @GetMapping("{user_id}")
    public String showDeclarations(@PathVariable int user_id, Model model){
        User user = userDAO.getUserProfile(user_id);
        List<DeclarationForm> declarationForms = user.getDeclarations();
        System.out.println(declarationForms);
        model.addAttribute("users", user);
        model.addAttribute("declarationForm", declarationForms);
        return "declaration";
    }

    @GetMapping("{user_id}/details/{declaration_id}")
    public String declarationDetails(@PathVariable int user_id,@PathVariable int declaration_id, Model model){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);

        if (declarationForm == null) {
            return "error";
        }

        model.addAttribute("declarationForm", declarationForm);
        return "declarationDetails";
    }

    @GetMapping("{user_id}/new")
    public String newDeclaration( @PathVariable int user_id,Model model){
        DeclarationForm declarationForm= new DeclarationForm();
        declarationForm.setStatus("Pending");
        model.addAttribute("form", declarationForm);
        model.addAttribute("user_id", user_id);
        return "add_declaration";
    }

    @PostMapping("{user_id}")
    public String saveDeclaration(@PathVariable int user_id, @ModelAttribute("form") DeclarationForm declarationForm, Model model) {
        declarationService.saveDeclaration(declarationForm,user_id);
        return "redirect:/declaration/"+ user_id;
    }

    @GetMapping("{user_id}/delete/{declaration_id}")
    public String deleteDeclaration(@PathVariable int user_id, @PathVariable int declaration_id, Model model){
        declarationService.deleteDeclaration(declaration_id);
        return "redirect:/declaration/"+ user_id;
    }

    @GetMapping("{user_id}/{declaration_id}")
    public String editDeclaration(@PathVariable int user_id, @PathVariable int declaration_id, Model model){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);
        model.addAttribute("form", declarationForm);
        model.addAttribute("user_id", user_id);
        return "add_declaration";
    }

    @PostMapping("{user_id}/{declaration_id}")
    public String updateDeclaration(@PathVariable int user_id, @PathVariable int declaration_id){
        System.out.println("user_id: (2 ids)"+user_id);

        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);
        User user = userDAO.getUserProfile(user_id);
        declarationService.saveDeclaration(declarationForm,user_id);
        return "redirect:/declaration/"+user_id;
    }



}
