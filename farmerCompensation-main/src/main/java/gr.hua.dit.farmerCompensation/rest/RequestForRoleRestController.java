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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class RequestForRoleRestController {

    @Autowired
    UserDAO userDAO;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private RequestForRoleRepository requestForRoleRepository;

    @Autowired
    private RequestForRoleService requestForRoleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("role/delete/{user_id}/{role_id}")
    public ResponseEntity<?> deleteRoleFromUser(@PathVariable int user_id, @PathVariable Integer role_id){
        User user = (User) userService.getUser(user_id);
        Role role = roleRepository.findById(role_id).get();

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

    @GetMapping("requests")
    public ResponseEntity<?> showRoleRequests() {
        List<RequestForRole> requestForRoles = requestForRoleService.getPendingRoleRequests();

        if(requestForRoles.isEmpty()){
            return ResponseEntity.badRequest().body("No Role request found");
        }else {
            return new ResponseEntity<>(requestForRoles,HttpStatus.OK);
        }
    }

    @PostMapping("requests/accept/{user_id}/{request_id}")
    public ResponseEntity<?> acceptRequest(@PathVariable int user_id, @PathVariable int request_id){
        RequestForRole request = requestForRoleService.getReport(request_id);

        User user = userDAO.getUserProfile(user_id);
        Role role = request.getRole();
        String userRole = userService.getUserRole();

        if ("ROLE_INSPECTOR".equals(role.getName()) && user.getRoles().stream().anyMatch(r -> "ROLE_FARMER".equals(r.getName()))) {
        request.setStatus("Approved");
        user.getRoles().add(role);
        request.setRole(role);

        userService.updateUser(user);
        requestForRoleService.saveRequest(request, user_id);
        requestForRoleService.deleteRequest(request_id);

        return new ResponseEntity<>("User's role request approved!",HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Role not found!", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("requests/reject/{user_id}/{request_id}")
    public ResponseEntity<?> rejectRequest(@PathVariable int user_id, @PathVariable int request_id,Model model){

        RequestForRole request = requestForRoleService.getReport(request_id);
        User user = userDAO.getUserProfile(user_id);
        String userRole = userService.getUserRole();

        request.setStatus("Rejected");

        requestForRoleService.saveRequest(request, user_id);
        requestForRoleService.deleteRequest(request_id);
        return new ResponseEntity<>("User's role request rejected!", HttpStatus.OK);

    }
}
