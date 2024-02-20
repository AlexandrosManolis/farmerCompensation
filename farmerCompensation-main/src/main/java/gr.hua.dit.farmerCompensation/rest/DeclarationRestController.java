package gr.hua.dit.farmerCompensation.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import gr.hua.dit.farmerCompensation.dao.DeclarationDAO;
import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.payload.response.MessageResponse;
import gr.hua.dit.farmerCompensation.repository.DeclarationRepository;
import gr.hua.dit.farmerCompensation.service.DeclarationService;
import gr.hua.dit.farmerCompensation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/declaration/{user_id}")
public class DeclarationRestController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private DeclarationService declarationService;

    @Autowired
    private DeclarationDAO declarationDAO;

    @Autowired
    private DeclarationRepository declarationRepository;

    @Autowired
    private UserService userService;

    //show all the declarations of a user
    @GetMapping("")
    public ResponseEntity<?> showDeclarations(@PathVariable int user_id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = userService.getUserRole();

        String username = authentication.getName();
        Integer userId= userDAO.getUserId(username);


        User user = userDAO.getUserProfile(user_id);
        List<DeclarationForm> declarationForms = user.getDeclarations();
        List<Map<String, Object>> result = new ArrayList<>();

        //accordingly with the role of the user can see all or some of the declarations
        if(userRole.equals("ROLE_ADMIN") || userRole.equals("ROLE_INSPECTOR")){
            for (DeclarationForm declarationForm : declarationForms) {
                Map<String, Object> declarationInfo = new HashMap<>();
                declarationInfo.put("id", declarationForm.getId());
                declarationInfo.put("description", declarationForm.getDescription());
                declarationInfo.put("submissionDate", declarationForm.getSubmissionDate());
                declarationInfo.put("userId", declarationForm.getUser().getId());
                declarationInfo.put("status", declarationForm.getStatus());
                declarationInfo.put("amount", declarationForm.getAmount());
                result.add(declarationInfo);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }else if(userRole.equals("ROLE_FARMER") && !userRole.equals("ROLE_INSPECTOR") && userId==user_id){
            for (DeclarationForm declarationForm : declarationForms) {
                Map<String, Object> declarationInfo = new HashMap<>();
                declarationInfo.put("id", declarationForm.getId());
                declarationInfo.put("description", declarationForm.getDescription());
                declarationInfo.put("submissionDate", declarationForm.getSubmissionDate());
                declarationInfo.put("userId", declarationForm.getUser().getId());
                declarationInfo.put("status", declarationForm.getStatus());
                declarationInfo.put("amount", declarationForm.getAmount());

                result.add(declarationInfo);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>("Unauthorized to show declarations!", HttpStatus.UNAUTHORIZED);
    }

    //see declaration details
    @GetMapping("details/{declaration_id}")
    public ResponseEntity<?> declarationDetails(@PathVariable int user_id,@PathVariable int declaration_id){
        //get the declaration
        DeclarationForm declarationForm = declarationDAO.getDeclaration(declaration_id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = userService.getUserRole();

        String username = authentication.getName();
        Integer userId= userDAO.getUserId(username);
        //return the details if the user has an acceptable role
        if (declarationForm == null) {
            return ResponseEntity.badRequest().body("No declaration found!");
        }else if(userRole.equals("ROLE_ADMIN") || userRole.equals("ROLE_INSPECTOR")){
            return new ResponseEntity<>(declarationForm, HttpStatus.OK);
        } else if (userRole.equals("ROLE_FARMER") && !userRole.equals("ROLE_INSPECTOR") && userId==user_id) {
            return new ResponseEntity<>(declarationForm, HttpStatus.OK);

        }
        return new ResponseEntity<>("Unauthorized to see the details", HttpStatus.UNAUTHORIZED);
    }

    //create new declaration
    @GetMapping("new")
    public ResponseEntity<?> newDeclaration(@PathVariable int user_id){
        DeclarationForm newDeclaration = new DeclarationForm();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = userService.getUserRole();

        String username = authentication.getName();
        Integer userId= userDAO.getUserId(username);

        if(userRole.equals("ROLE_ADMIN")){

            return new ResponseEntity<>(newDeclaration,HttpStatus.OK);
        } else if(user_id == userId) {
            return new ResponseEntity<>(newDeclaration,HttpStatus.OK);
        }else{
            return ResponseEntity.badRequest().body("You can't save the declaration!");
        }

    }

    //create new declaration
    @PostMapping("new")
    public ResponseEntity<?> saveDeclaration(@PathVariable int user_id, @RequestBody DeclarationForm declarationForm) {
        DeclarationForm newDeclarationForm = new DeclarationForm();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = userService.getUserRole();

        String username = authentication.getName();
        Integer userId= userDAO.getUserId(username);

        //Accordingly with the role user has create a new declaration filling in all the fields and set status to pending
        if(userRole.equals("ROLE_ADMIN")){
            newDeclarationForm.setAnnualStartProduction(declarationForm.getAnnualStartProduction());
            newDeclarationForm.setDamageDate(declarationForm.getDamageDate());
            newDeclarationForm.setDescription(declarationForm.getDescription());
            newDeclarationForm.setFieldAddress(declarationForm.getFieldAddress());
            newDeclarationForm.setFieldSize(declarationForm.getFieldSize());
            newDeclarationForm.setPlant_production(declarationForm.getPlant_production());
            newDeclarationForm.setStatus("Pending");

            newDeclarationForm.setSubmissionDate(getCurrentDate());

            declarationService.saveDeclaration(newDeclarationForm,user_id);
            return new ResponseEntity<>(newDeclarationForm,HttpStatus.OK);
        } else if(user_id == userId) {

            newDeclarationForm.setAnnualStartProduction(declarationForm.getAnnualStartProduction());
            newDeclarationForm.setDamageDate(declarationForm.getDamageDate());
            newDeclarationForm.setDescription(declarationForm.getDescription());
            newDeclarationForm.setFieldAddress(declarationForm.getFieldAddress());
            newDeclarationForm.setFieldSize(declarationForm.getFieldSize());
            newDeclarationForm.setPlant_production(declarationForm.getPlant_production());
            newDeclarationForm.setStatus("Pending");

            newDeclarationForm.setSubmissionDate(getCurrentDate());

            declarationService.saveDeclaration(newDeclarationForm,user_id);
            return new ResponseEntity<>(newDeclarationForm,HttpStatus.OK);
        }else{
            return ResponseEntity.badRequest().body("You can't save the declaration!");
        }
    }

    //take the current date for the submission
    private Date getCurrentDate() {

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


        try {
            String formattedDate = dateFormat.format(currentDate);
            return dateFormat.parse(formattedDate);
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception as needed
            return null;
        }
    }


    //delete the declaration
    @GetMapping("delete/{declaration_id}")
    public ResponseEntity<?> deleteDeclaration(@PathVariable int user_id, @PathVariable int declaration_id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = userService.getUserRole();

        String username = authentication.getName();
        Integer userId= userDAO.getUserId(username);

        //get the declaration and if the condition is true excecute delete of the declaration
        DeclarationForm declarationForm= declarationDAO.getDeclaration(declaration_id);
        if(declarationForm==null){
            return new ResponseEntity<>("declaration does not exist", HttpStatus.NOT_FOUND);
        }else if(userRole.equals("ROLE_ADMIN")){
            declarationService.deleteDeclaration(declaration_id);
            return new ResponseEntity<>("declaration deleted!",HttpStatus.OK);
        }else if(userId==user_id){
            declarationService.deleteDeclaration(declaration_id);
            return new ResponseEntity<>("declaration deleted!",HttpStatus.OK);
        }else{
            return ResponseEntity.badRequest().body("you cant delete the declaration");
        }
    }

    //get the current data before edit
    @GetMapping("edit/{declaration_id}")
    public ResponseEntity<?> editDeclaration(@PathVariable int user_id, @PathVariable int declaration_id){
        DeclarationForm updatedDeclarationForm = declarationDAO.getDeclaration(declaration_id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = userService.getUserRole();

        String username = authentication.getName();
        Integer userId= userDAO.getUserId(username);

        if(updatedDeclarationForm==null){
            return ResponseEntity.badRequest().body("Declaration doesn't exist");
        }else if(userRole.equals("ROLE_ADMIN")){

            return new ResponseEntity<>(updatedDeclarationForm,HttpStatus.OK);

        } else if(user_id == userId) {

            return new ResponseEntity<>(updatedDeclarationForm,HttpStatus.OK);

        }else{
            return ResponseEntity.badRequest().body("You can't edit the declaration!");
        }
    }

    //save the new edited declaration
    @PostMapping("edit/{declaration_id}")
    public ResponseEntity<?> updateDeclaration(@RequestBody DeclarationForm declarationForm,@PathVariable int user_id, @PathVariable int declaration_id){
        //get the declaration if exists
        DeclarationForm updatedDeclaration = declarationDAO.getDeclaration(declaration_id);
        User user = userDAO.getUserProfile(user_id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = userService.getUserRole();

        String username = authentication.getName();
        Integer userId= userDAO.getUserId(username);

        //Accordingly to the role of the user set the new values, set status again to Pending and set to the current date again.
        if(updatedDeclaration==null){
            return ResponseEntity.badRequest().body("Declaration doesn't exist");
        }else if(userRole.equals("ROLE_ADMIN")){
            updatedDeclaration.setAnnualStartProduction(declarationForm.getAnnualStartProduction());
            updatedDeclaration.setDamageDate(declarationForm.getDamageDate());
            updatedDeclaration.setDescription(declarationForm.getDescription());
            updatedDeclaration.setFieldAddress(declarationForm.getFieldAddress());
            updatedDeclaration.setFieldSize(declarationForm.getFieldSize());
            updatedDeclaration.setPlant_production(declarationForm.getPlant_production());
            updatedDeclaration.setStatus("Pending");

            updatedDeclaration.setSubmissionDate(getCurrentDate());


            declarationService.saveDeclaration(updatedDeclaration,user_id);
            return ResponseEntity.ok(new MessageResponse("Declaration has been saved successfully!"));

        } else if(user_id == userId) {
            updatedDeclaration.setAnnualStartProduction(declarationForm.getAnnualStartProduction());
            updatedDeclaration.setDamageDate(declarationForm.getDamageDate());
            updatedDeclaration.setDescription(declarationForm.getDescription());
            updatedDeclaration.setFieldAddress(declarationForm.getFieldAddress());
            updatedDeclaration.setFieldSize(declarationForm.getFieldSize());
            updatedDeclaration.setPlant_production(declarationForm.getPlant_production());
            updatedDeclaration.setStatus("Pending");

            updatedDeclaration.setSubmissionDate(getCurrentDate());


            declarationService.saveDeclaration(updatedDeclaration,user_id);
            return ResponseEntity.ok(new MessageResponse("Declaration has been saved successfully!"));

        }else{
            return ResponseEntity.badRequest().body("You can't update the declaration!");
        }

    }

}
