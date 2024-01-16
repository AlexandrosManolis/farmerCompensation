package gr.hua.dit.farmerCompensation.controller;

import gr.hua.dit.farmerCompensation.config.JwtUtils;
import gr.hua.dit.farmerCompensation.entity.Role;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.payload.request.LoginRequest;
import gr.hua.dit.farmerCompensation.payload.request.SignupRequest;
import gr.hua.dit.farmerCompensation.payload.response.JwtResponse;
import gr.hua.dit.farmerCompensation.payload.response.MessageResponse;
import gr.hua.dit.farmerCompensation.repository.RoleRepository;
import gr.hua.dit.farmerCompensation.repository.UserRepository;
import gr.hua.dit.farmerCompensation.service.DeclarationService;
import gr.hua.dit.farmerCompensation.service.UserDetailsImpl;
import gr.hua.dit.farmerCompensation.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DeclarationService declarationService;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
     AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

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
            adminUser.setAfm("123456789");
            adminUser.setAddress("admin_address");
            adminUser.setFull_name("Admin");
            adminUser.setIdentity_id("AD123456");
            adminUser.setPassword(new BCryptPasswordEncoder().encode("admin"));
            adminUser.getRoles().add(adminRole);

            userRepository.save(adminUser);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("authentication");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        System.out.println("authentication: " + authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("post authentication");
        String jwt = jwtUtils.generateJwtToken(authentication);
        System.out.println("jw: " + jwt);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getEmail(),signUpRequest.getUsername(),encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFull_name(),signUpRequest.getAddress(),signUpRequest.getAfm(), signUpRequest.getIdentity_id()
                );

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName("ROLE_FARMER")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}

