package gr.hua.dit.farmerCompensation.controller;

import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.RequestForRole;
import gr.hua.dit.farmerCompensation.entity.Role;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.repository.RoleRepository;
import gr.hua.dit.farmerCompensation.service.RequestForRoleService;
import gr.hua.dit.farmerCompensation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class RequestForRoleController {

    @Autowired
    UserDAO userDAO;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private RequestForRoleService requestForRoleService;

    @Autowired
    private UserService userService;

    @PostMapping("role/add/{user_id}/{role_id}")
    public String requestRole(@PathVariable int user_id, @RequestParam int role_id, Model model){
        User user = (User) userDAO.getUserProfile(user_id);
        Role role = roleRepository.findById(role_id).get();

        RequestForRole requestForRole= new RequestForRole();
        requestForRole.setStatus("Pending");
        requestForRole.setRole(role);

        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());

        requestForRoleService.saveRequest(requestForRole, user_id);
        return "redirect:/users";
    }

    @GetMapping("requests")
    @PreAuthorize("hasRole('ADMIN')")
    public String showRoleRequests(Model model) {
        List<User> usersWithRoleRequests = requestForRoleService.getUsersWithRoleRequests();
        List<RequestForRole> requestForRoles = requestForRoleService.getPendingRoleRequests();

        model.addAttribute("usersWithRoleRequests", usersWithRoleRequests);
        model.addAttribute("requestForRoles", requestForRoles);

        return "requests";
    }

//    @GetMapping("requests/accept/{user_id}/{request_id}")
//    public String AcceptRequest(@RequestParam int user_id, @PathVariable int requests_id, Model model){
//        RequestForRole request = requestForRoleService.getReport(requests_id);
//
//        model.addAttribute("request", request);
//        model.addAttribute("user_id", user_id);
//
//        return "requests";
//    }

    @PostMapping("requests/accept/{user_id}/{request_id}")
    public String acceptRequest(@PathVariable int user_id, @PathVariable int request_id, Model model){
        RequestForRole request = requestForRoleService.getReport(request_id);
        User user = userDAO.getUserProfile(user_id);
        Role role = request.getRole();

        if (role != null) {
        request.setStatus("Approved");
        user.getRoles().add(role);
        request.setRole(role);

        userService.updateUser(user);
        requestForRoleService.saveRequest(request, user_id);
        requestForRoleService.deleteRequest(request_id);
        return "redirect:/users/requests";
        }else {
            return "redirect:/error";
        }
    }

    @PostMapping("requests/reject/{user_id}/{request_id}")
    public String rejectRequest(@PathVariable int user_id, @PathVariable int request_id,Model model){

        RequestForRole request = requestForRoleService.getReport(request_id);
        User user = userDAO.getUserProfile(user_id);

        request.setStatus("Rejected");

        requestForRoleService.saveRequest(request, user_id);
        requestForRoleService.deleteRequest(request_id);
        return "redirect:/users/requests";
    }
}
