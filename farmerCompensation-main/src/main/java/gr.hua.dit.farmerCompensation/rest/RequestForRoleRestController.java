package gr.hua.dit.farmerCompensation.rest;

import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.RequestForRole;
import gr.hua.dit.farmerCompensation.entity.Role;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.payload.response.MessageResponse;
import gr.hua.dit.farmerCompensation.repository.RequestForRoleRepository;
import gr.hua.dit.farmerCompensation.repository.RoleRepository;
import gr.hua.dit.farmerCompensation.repository.UserRepository;
import gr.hua.dit.farmerCompensation.service.RequestForRoleService;
import gr.hua.dit.farmerCompensation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/users")
public class RequestForRoleRestController {

    @Autowired
    UserDAO userDAO;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private RequestForRoleService requestForRoleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    //delete role from a user inspector
    @GetMapping("role/delete/{user_id}/{role_id}")
    public ResponseEntity<?> deleteRoleFromUser(@PathVariable int user_id, @PathVariable Integer role_id){
        User user = (User) userService.getUser(user_id);
        Role role = roleRepository.findById(role_id).get();

        //check if user has role inspector and delete it
        boolean hasInspectorRole = user.getRoles().stream()
                .anyMatch(userRole -> "ROLE_INSPECTOR".equals(userRole.getName()));

        if((role != null && "ROLE_INSPECTOR".equals(role.getName()) ) && hasInspectorRole){

            user.getRoles().remove(role);
            System.out.println("Roles: "+user.getRoles());
            userService.updateUser(user);

            return ResponseEntity.ok(new MessageResponse("Role deleted successfully!"));
        }else {
            return ResponseEntity.badRequest().body(new MessageResponse("Delete was not accepted"));
        }

    }

    //get role request
    @GetMapping("requests")
    public ResponseEntity<?> showRoleRequests() {
        List<RequestForRole> requestForRoles = requestForRoleService.getPendingRoleRequests();

        if(requestForRoles.isEmpty()){
            return ResponseEntity.ok(requestForRoles);
        }else {
            return new ResponseEntity<>(requestForRoles,HttpStatus.OK);
        }
    }

    //accept request
    @PostMapping("requests/accept/{user_id}/{request_id}")
    public ResponseEntity<?> acceptRequest(@PathVariable int user_id, @PathVariable int request_id){
        RequestForRole request = requestForRoleService.getReport(request_id);

        //get user and the requested role
        User user = userDAO.getUserProfile(user_id);
        Role role = request.getRole();
        String userRole = userService.getUserRole();

        //if user's role is farmer and asks to become inspector set status to approved, add role and update user
        if ("ROLE_INSPECTOR".equals(role.getName()) && user.getRoles().stream().anyMatch(r -> "ROLE_FARMER".equals(r.getName()))) {
        request.setStatus("Approved");
        user.getRoles().add(role);
        request.setRole(role);

        userService.updateUser(user);
        requestForRoleService.saveRequest(request, user_id);
        //delete the request
        requestForRoleService.deleteRequest(request_id);

        return new ResponseEntity<>("user's role request approved!",HttpStatus.OK);
        }else {
            return new ResponseEntity<>("role not found!", HttpStatus.NOT_FOUND);
        }
    }

    //reject request
    @PostMapping("requests/reject/{user_id}/{request_id}")
    public ResponseEntity<?> rejectRequest(@PathVariable int user_id, @PathVariable int request_id,Model model){
        //get user's request
        RequestForRole request = requestForRoleService.getReport(request_id);
        User user = userDAO.getUserProfile(user_id);
        String userRole = userService.getUserRole();
        //set status to rejected, and delete the request
        request.setStatus("Rejected");

        requestForRoleService.saveRequest(request, user_id);
        requestForRoleService.deleteRequest(request_id);
        return new ResponseEntity<>("user's role request rejected!", HttpStatus.OK);

    }
}
