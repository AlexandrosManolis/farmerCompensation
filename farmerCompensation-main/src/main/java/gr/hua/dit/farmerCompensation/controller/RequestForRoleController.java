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
        requestForRole.setRole_status("Pending");
        requestForRole.setRole(role);

        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());

        requestForRoleService.saveRequest(requestForRole, user_id);
        return "redirect:/users";
    }

    @GetMapping("requests")
    @PreAuthorize("hasRole('ADMIN')")
    public String showRoleRequests(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<User> usersWithRoleRequests = requestForRoleService.getUsersWithRoleRequests();
        List<RequestForRole> requestForRoles = requestForRoleService.getPendingRoleRequests();

        model.addAttribute("usersWithRoleRequests", usersWithRoleRequests);
        model.addAttribute("requestForRoles", requestForRoles);

        return "requests";
    }

    @GetMapping("{user_id}")
    public String showDeclarations(@PathVariable int user_id, Model model){
        User user = userDAO.getUserProfile(user_id);
        List<DeclarationForm> declarationForms = user.getDeclarations();
        System.out.println(declarationForms);
        model.addAttribute("users", user);
        model.addAttribute("declarationForm", declarationForms);
        return "declaration";
    }

//    @GetMapping("requests/{user_id}/accept")
//    public String acceptRoletoUser(@PathVariable Long user_id, @RequestParam Integer role_id, Model model){
//        User user = (User) userService.getUser(user_id);
//        Role role = roleRepository.findById(role_id).get();
//        user.getRoles().add(role);
//        RequestForRole requestForRole= requestForRoleService.getPendingRoleRequestById(user_id);
//
//        requestForRole.setRole_status("Accepted");
//
//        userService.updateUser(user);
//        requestForRoleService.updateRequest(requestForRole);
//
//        model.addAttribute("users", userService.getUsers());
//        model.addAttribute("roles", roleRepository.findAll());
//
//        return "users";
//
//    }
}
