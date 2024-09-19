package gr.hua.dit.farmerCompensation.rest;

import gr.hua.dit.farmerCompensation.dao.DeclarationDAO;
import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.service.DeclarationService;
import gr.hua.dit.farmerCompensation.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/declaration/report/{user_id}")
public class ReportsRestController {

    @Autowired
    private DeclarationDAO declarationDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private DeclarationService declarationService;

    @Autowired
    private EmailService emailService;

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

    private static final int DAYS_IN_MONTH = 30; // Assuming a month has 30 days

    private Date getRandomDateWithinNextMonth() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Calculate the date 1 month later
        calendar.add(Calendar.MONTH, 1);
        Date oneMonthLaterDate = calendar.getTime();

        // Generate a random number of days between 0 and 30
        Random random = new Random();
        int randomDays = random.nextInt(31); // 31 to include 30 as well

        // Add the random number of days to the current date
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, randomDays);

        return calendar.getTime();
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
            declarationForm.setAppointementDate(getRandomDateWithinNextMonth());
            declarationService.saveDeclaration(declarationForm,user_id);
            
            emailService.sendEmail(user.getEmail(),"The status of declaration request has changed","Dear "+user.getFull_name()+",\n The status of your declaration request has been changed to Check on site!\n For further information, please contact us.");
            return ResponseEntity.ok(Map.of("message", "Status changed to Check on site!"));
        }
    }

    //using to change status to accepted after call this method and set amount of compensation
    @GetMapping("acceptReport/{declaration_id}")
    public ResponseEntity<?> AcceptReport(@PathVariable int user_id, @PathVariable int declaration_id, @RequestParam String amount, @RequestBody DeclarationForm declarationForm){
        DeclarationForm acceptedDeclarationForm = declarationDAO.getDeclaration(declaration_id);
        User user = userDAO.getUserProfile(user_id);

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
        emailService.sendEmail(user.getEmail(),"Declaration request accepted","Dear "+user.getFull_name()+",\n your declaration request has been accepted!\n For further information, please contact us.");

        return new ResponseEntity<>(declarationForm,HttpStatus.OK);
    }

    @PostMapping("damageReport/{declaration_id}")
    public ResponseEntity<?> damageReport(@PathVariable int user_id, @PathVariable int declaration_id, @RequestParam Double damagePercentage, Model model){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);
        User user = userDAO.getUserProfile(user_id);

        double decimalValue = damagePercentage / 100.0;
        // Create a BigDecimal with the decimal value
        var percentage = BigDecimal.valueOf(decimalValue);

        declarationForm.setDamagePercentage(percentage);

        double fieldSize = Double.parseDouble(declarationForm.getFieldSize());
        double annualRevenues = Double.parseDouble(declarationForm.getAnnualRevenues());

        var estimation =  ( fieldSize * (declarationForm.getDamagePercentage().doubleValue() - 0.15) * annualRevenues * 0.88) /100;
        String refund = String.valueOf(estimation);
        declarationForm.setEstimatedRefund(refund);

        declarationService.saveDeclaration(declarationForm,user_id);
        return new ResponseEntity<>(declarationForm,HttpStatus.OK);
    }

    //using to change status to rejected after call this method
    @PostMapping("rejectReport/{declaration_id}")
    public ResponseEntity<?> rejectReport(@PathVariable int user_id, @PathVariable int declaration_id, @RequestParam String rejectCause,Model model){
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);

        User user = userDAO.getUserProfile(user_id);
        declarationForm.setStatus("Rejected");
        declarationForm.setRejectCause(rejectCause);

        declarationService.saveDeclaration(declarationForm,user_id);
        emailService.sendEmail(user.getEmail(),"Declaration request rejected","Dear "+user.getFull_name()+",\n your declaration request has been rejected!\n For further information, please contact us.");
        return new ResponseEntity<>(declarationForm,HttpStatus.OK);
    }

}
