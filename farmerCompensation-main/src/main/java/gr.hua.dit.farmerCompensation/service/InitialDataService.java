package gr.hua.dit.farmerCompensation.service;

import gr.hua.dit.farmerCompensation.entity.Role;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.repository.DeclarationRepository;
import gr.hua.dit.farmerCompensation.repository.RequestForRoleRepository;
import gr.hua.dit.farmerCompensation.repository.RoleRepository;
import gr.hua.dit.farmerCompensation.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class InitialDataService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    private final RequestForRoleRepository requestForRoleRepository;
    private final DeclarationRepository declarationRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialDataService(UserRepository userRepository,
                              RoleRepository roleRepository,
                              RequestForRoleRepository requestForRoleRepository,
                              DeclarationRepository declarationRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.requestForRoleRepository= requestForRoleRepository;
        this.declarationRepository = declarationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void createRolesAndUsers() {
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

    @PostConstruct
    public void setup(){
        this.createRolesAndUsers();
    }



}
