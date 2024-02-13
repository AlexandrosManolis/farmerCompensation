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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    BCryptPasswordEncoder encoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
     AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;


    @PostMapping("signin")
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

    @PostMapping("signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        if (userRepository.existsByAfm(user.getAfm())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Afm is already in use!"));
        }

//        // Create new user's account
//        User newuser = new User();
//        newuser.setEmail(user.getEmail());
//        newuser.setUsername(user.getUsername());
//        newuser.setPassword(this.passwordEncoder.encode(user.getPassword()));
//        newuser.setFull_name(user.getFull_name());
//        newuser.setAddress(user.getAddress());
//        newuser.setAfm(user.getAfm());
//        newuser.setIdentity_id(user.getIdentity_id());

        User newuser= new User(user.getEmail(), user.getUsername(),encoder.encode(user.getPassword()),user.getFull_name(),user.getAddress(),user.getAfm(),user.getIdentity_id());

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("ROLE_FARMER").orElseThrow(()-> new RuntimeException("Farmer role not found")));
        newuser.setRoles(roles);

        userRepository.save(newuser);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

}

