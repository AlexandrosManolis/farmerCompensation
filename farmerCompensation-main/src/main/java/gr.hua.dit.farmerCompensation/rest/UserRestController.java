package gr.hua.dit.farmerCompensation.rest;

import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.RequestForRole;
import gr.hua.dit.farmerCompensation.entity.Role;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.payload.response.MessageResponse;
import gr.hua.dit.farmerCompensation.repository.DeclarationRepository;
import gr.hua.dit.farmerCompensation.repository.RoleRepository;
import gr.hua.dit.farmerCompensation.repository.UserRepository;
import gr.hua.dit.farmerCompensation.service.DeclarationService;
import gr.hua.dit.farmerCompensation.service.RequestForRoleService;
import gr.hua.dit.farmerCompensation.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserRestController {


    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    DeclarationRepository declarationRepository;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RequestForRoleService requestForRoleService;

    @Autowired
    private DeclarationService declarationService;

    @Autowired
    private UserRepository userRepository;



    @GetMapping("")
    public ResponseEntity<?> showUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = userService.getUserRole();

        String username = authentication.getName();
        Integer userId= userDAO.getUserId(username);

            if (userRole.equals("ROLE_FARMER") && !userRole.equals("ROLE_INSPECTOR")) {

                if (userId == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                User user = userDAO.getUserProfile(userId);

                if (user == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                Integer userProfileId = user.getId();
                String userProfileUsername = user.getUsername();
                String userProfileEmail = user.getEmail();

                // Creating a response map with the required fields
                Map<String, Object> responseMap = Map.of(
                        "id", userProfileId,
                        "username", userProfileUsername,
                        "email", userProfileEmail
                );

                return new ResponseEntity<>(responseMap, HttpStatus.OK);
            } else if (userRole.equals("ROLE_INSPECTOR") ) {
                List<User> declarationUsers = declarationService.getUsersWithDeclarations();
                User user= userService.getUserProfile(userId);

                List<User> combinedList = new ArrayList<>();
                if (!declarationUsers.isEmpty()) {
                    combinedList.addAll(declarationUsers);
                }
                if (!declarationUsers.contains(user)) {
                    combinedList.add(user);
                }
                
                List<User> userList = new ArrayList<>();

                for (User userDetails : combinedList) {
                    Integer userProfileId = userDetails.getId();
                    String userProfileUsername = userDetails.getUsername();
                    String userProfileEmail = userDetails.getEmail();

                    User user1 = new User();
                    user1.setId(userProfileId);
                    user1.setUsername(userProfileUsername);
                    user1.setEmail(userProfileEmail);
                    userList.add(user1);
                }

                return new ResponseEntity<>(userList, HttpStatus.OK);

            }else if(userRole.equals("ROLE_ADMIN")){
                    List<User> users = userService.getUsers();
                    return new ResponseEntity<>(users, HttpStatus.OK);

            }else {
                return new ResponseEntity<>("No users found", HttpStatus.NOT_FOUND);
            }

        }

    @GetMapping("details/{user_id}")
    public ResponseEntity<?> userDetails(@PathVariable int user_id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = userService.getUserRole();

        User user = userDAO.getUserProfile(user_id);

        String username = authentication.getName();
        Integer userId= userDAO.getUserId(username);

        if (((userRole.equals("ROLE_FARMER")) && !(userRole.equals("ROLE_INSPECTOR"))) && (userId != user_id)) {

            return new ResponseEntity<>("Unauthorized to see other user's details", HttpStatus.UNAUTHORIZED);
        }
        else if("admin".equals(user.getUsername()) && !(userRole.equals("ROLE_ADMIN")) ){

            return new ResponseEntity<>("Unauthorized to see admin details", HttpStatus.UNAUTHORIZED);
        }else if (user == null) {
            // Handle case where the declarationForm with the specified ID was not found
            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND); // For example, redirect to an error page
        }else {

            return new ResponseEntity<>(user, HttpStatus.OK);
        }

    }

    @GetMapping("edit/{user_id}")
    public ResponseEntity<?> editUser(@PathVariable int user_id) {
        User existingUser = userService.getUserProfile(user_id);

        if (existingUser == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        return new ResponseEntity<>(existingUser, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("edit/{user_id}")
    public ResponseEntity<?> saveUser(@PathVariable Integer user_id, @RequestBody User user) {
        User the_user = (User) userService.getUser(user_id);

        if (the_user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found"));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = userService.getUserRole();

        String username = authentication.getName();
        Integer userId= userDAO.getUserId(username);
        
        if (userRole.equals("ROLE_ADMIN") || ((userId == user_id) && (userRole.equals("ROLE_FARMER") || userRole.equals("ROLE_INSPECTOR")))){
            the_user.setUsername(user.getUsername());
            the_user.setEmail(user.getEmail());
            the_user.setAddress(user.getAddress());
            the_user.setAfm(user.getAfm());
            the_user.setFull_name(user.getFull_name());
            the_user.setIdentity_id(user.getIdentity_id());

            try{
                userDAO.saveUser(the_user);

                if (authentication != null && authentication.getPrincipal() instanceof User) {
                    User userDetails = (User) authentication.getPrincipal();
                    if (userDetails.getUsername().equals(the_user.getUsername())) {
                        // Update the user details in the principal
                        ((User) authentication.getPrincipal()).setUsername(the_user.getUsername());
                        ((User) authentication.getPrincipal()).setEmail(the_user.getEmail());
                    }
                }
                return ResponseEntity.ok(new MessageResponse("User has been saved successfully!"));

            }catch (Exception e) {

                String errorMessage = "Error saving user: " + e.getMessage();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(errorMessage));
            }
            
        } else {
            return ResponseEntity.badRequest().body("Unauthorized user!");
        }
    }

    @PostMapping("role/add/{user_id}")
    public ResponseEntity<?> requestRole(@PathVariable int user_id){
        String userRole = userService.getUserRole();

        String requestedRole = "ROLE_INSPECTOR";
        Optional<Role> roleRequest = roleRepository.findByName(requestedRole);
        Role requestRole = roleRequest.get();
        Integer role_id = requestRole.getId();

        User user = (User) userDAO.getUserProfile(user_id);
        Role role = roleRepository.findById(role_id).get();
        List<User> usersWithRoles = requestForRoleService.getUsersWithRoleRequests();

        if(userRole.equals("ROLE_FARMER") && !userRole.equals("ROLE_INSPECTOR") && !role.equals("ROLE_ADMIN")){

            for(User checkUser : usersWithRoles){
                Integer checkedUser = checkUser.getId();
                if(user.getId() == checkedUser){
                    return ResponseEntity.badRequest().body(new MessageResponse("User has already a request!"));
                }
                }

            RequestForRole requestForRole= new RequestForRole();
            requestForRole.setStatus("Pending");
            requestForRole.setRole(role);

            requestForRoleService.saveRequest(requestForRole, user_id);
            return ResponseEntity.ok(new MessageResponse("Request for user has been saved successfully!"));

        }else {
            return new ResponseEntity<>("User has no only role farmer", HttpStatus.UNAUTHORIZED);
        }
    }

}
