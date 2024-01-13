package gr.hua.dit.farmerCompensation.controller;

import gr.hua.dit.farmerCompensation.dao.DeclarationDAO;
import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.service.DeclarationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/declaration/{user_id}")
public class ReportsController {

    @Autowired
    private DeclarationDAO declarationDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private DeclarationService declarationService;

    @GetMapping("report/{declaration_id}")
    public String declarationReport(@PathVariable int user_id, @PathVariable int declaration_id, Model model){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);
        User user = userDAO.getUserProfile(user_id);

        if (declarationForm == null) {
            return "error";
        }
        model.addAttribute("declarationForm", declarationForm);
        model.addAttribute("user", user);
        return "declarationDetails";
    }

    @PostMapping("checkonsite/{declaration_id}")
    public String ChangeToCheck(@PathVariable int user_id, @PathVariable int declaration_id, Model model){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);
        User user = userDAO.getUserProfile(user_id);
        declarationForm.setStatus("Check on site");

        declarationService.saveDeclaration(declarationForm,user_id);
        return "redirect:/declaration/"+user_id;
    }

    @GetMapping("acceptReport/{declaration_id}")
    public String StatusReport(@PathVariable int user_id, @PathVariable int declaration_id, @RequestParam String amount, Model model){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);

        model.addAttribute("declaration", declarationForm);
        model.addAttribute("user_id", user_id);

        return "declaration";
    }

    @PostMapping("acceptReport/{declaration_id}")
    public String statusReport(@PathVariable int user_id, @PathVariable int declaration_id, @RequestParam String amount, Model model){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);
        User user = userDAO.getUserProfile(user_id);

        declarationForm.setStatus("Accepted");
        declarationForm.setAmount(amount);

        declarationService.saveDeclaration(declarationForm,user_id);
        return "redirect:/declaration/"+user_id;
    }

    @PostMapping("rejectReport/{declaration_id}")
    public String rejectReport(@PathVariable int user_id, @PathVariable int declaration_id, Model model){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);
        User user = userDAO.getUserProfile(user_id);
        declarationForm.setStatus("Rejected");

        declarationService.saveDeclaration(declarationForm,user_id);
        return "redirect:/declaration/"+user_id;
    }

}
