package gr.hua.dit.farmerCompensation.controller;

import gr.hua.dit.farmerCompensation.dao.DeclarationDAO;
import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.Role;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.repository.RoleRepository;
import gr.hua.dit.farmerCompensation.repository.UserRepository;
import gr.hua.dit.farmerCompensation.service.DeclarationService;
import gr.hua.dit.farmerCompensation.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DeclarationService declarationService;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void setup() {
        roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            roleRepository.save(new Role("ROLE_ADMIN"));
            return null;
        });
        roleRepository.findByName("ROLE_FARMER").orElseGet(() -> {
            roleRepository.save(new Role("ROLE_FARMER"));
            return null;
        });
        roleRepository.findByName("ROLE_INSPECTOR").orElseGet(() -> {
            roleRepository.save(new Role("ROLE_INSPECTOR"));
            return null;
        });

        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(new BCryptPasswordEncoder().encode("admin")); // Use a password encoder in production
            adminUser.getRoles().add(adminRole);

            userRepository.save(adminUser);
        }
    }



    @GetMapping("")
    public String home(){

        return "home";
    }

    @GetMapping("/login")
    public String login(){

        return "login" ;
    }

    @GetMapping("/register")
    public String register(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "user_registration";
    }
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, Model model){
        System.out.println("Roles: "+user.getRoles());
        Integer id = userService.saveUser(user);
        String message = "User '"+id+"' saved successfully !";
        model.addAttribute("msg", message);
        return "home";
    }

    @GetMapping("/users")
    public String showUsers(Model model){
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "users";
    }

    @GetMapping("/users/edit/{user_id}")
    public String editUser(@PathVariable Long user_id, Model model){
        model.addAttribute("user", userService.getUser(user_id));
        return "edit_user";
    }

    @PostMapping("/users/edit/{user_id}")
    public String saveUser(@PathVariable Long user_id, @ModelAttribute("user") User user, Model model) {
        User the_user = (User) userService.getUser(user_id);
        the_user.setEmail(user.getEmail());
        the_user.setUsername(user.getUsername());
        userDAO.saveUser(the_user);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User userDetails = (User) authentication.getPrincipal();
            if (userDetails.getUsername().equals(the_user.getUsername())) {
                // Update the user details in the principal
                ((User) authentication.getPrincipal()).setUsername(the_user.getUsername());
                ((User) authentication.getPrincipal()).setEmail(the_user.getEmail());
            }
        }
        model.addAttribute("users", userService.getUsers());
        return "redirect:/users";
    }

    @GetMapping("users/details/{user_id}")
    public String userDetails(@PathVariable int user_id,Model model){
        User user = userDAO.getUserProfile(user_id);

        if (user == null) {
            // Handle case where the declarationForm with the specified ID was not found
            return "error"; // For example, redirect to an error page
        }

        model.addAttribute("user", user);
        return "userDetails";
    }

    @GetMapping("/users/role/delete/{user_id}/{role_id}")
    public String deleteRolefromUser(@PathVariable Long user_id, @PathVariable Integer role_id, Model model){
        User user = (User) userService.getUser(user_id);
        Role role = roleRepository.findById(role_id).get();
        user.getRoles().remove(role);
        System.out.println("Roles: "+user.getRoles());
        userService.updateUser(user);
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "users";

    }

    @GetMapping("/users/role/add/{user_id}/{role_id}")
    public String addRoletoUser(@PathVariable Long user_id, @PathVariable Integer role_id, Model model){
        User user = (User) userService.getUser(user_id);
        Role role = roleRepository.findById(role_id).get();
        user.getRoles().add(role);
        System.out.println("Roles: "+user.getRoles());
        userService.updateUser(user);
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "users";

    }

}

