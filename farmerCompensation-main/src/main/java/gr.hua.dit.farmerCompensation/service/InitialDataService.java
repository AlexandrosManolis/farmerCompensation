package gr.hua.dit.farmerCompensation.service;

import gr.hua.dit.farmerCompensation.entity.Role;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.repository.DeclarationRepository;
import gr.hua.dit.farmerCompensation.repository.RequestForRoleRepository;
import gr.hua.dit.farmerCompensation.repository.RoleRepository;
import gr.hua.dit.farmerCompensation.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class InitialDataService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RequestForRoleRepository requestForRoleRepository;

    @Autowired
    private DeclarationRepository declarationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
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

        final List<String> rolesToCreate = List.of("ROLE_ADMIN", "ROLE_FARMER", "ROLE_INSPECTOR");
        for (final String roleName : rolesToCreate) {
            roleRepository.findByName(roleName).orElseGet(() -> {
                roleRepository.save(new Role(roleName));
                return null;
            });
        }

        userRepository.findByUsername("admin").orElseGet(()-> {

            User adminUser = new User("admin@example.com","admin", this.passwordEncoder.encode("admin"),"Admin","admin_address","123456789","AD123456");
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_ADMIN").orElseThrow(()-> new RuntimeException("Admin role not found")));
            adminUser.setRoles(roles);

            userRepository.save(adminUser);
            return null;
        });

        userRepository.findByUsername("user1").orElseGet(() -> {
            User user = new User("user1@gmail.com", "user1", this.passwordEncoder.encode("12345"),"user1 fullName","Address 1", "012345678","AM234567");
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_FARMER").orElseThrow(()-> new RuntimeException("Farmer role not found")));
            user.setRoles(roles);
            userRepository.save(user);
            return null;
        });

        userRepository.findByUsername("user2").orElseGet(() -> {
            User user = new User("user2@gmail.com", "user2", this.passwordEncoder.encode("54321"),"user2 fullName","Address 2", "234567890","AM012345");
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_FARMER").orElseThrow(()-> new RuntimeException("Farmer role not found")));
            roles.add(roleRepository.findByName("ROLE_INSPECTOR").orElseThrow(()-> new RuntimeException("Inspector role not found")));
            user.setRoles(roles);
            userRepository.save(user);
            return null;
        });

        userRepository.findByUsername("user3").orElseGet(() -> {
            User user = new User("user3@gmail.com", "user3", this.passwordEncoder.encode("123"),"user3 fullName","Address 3", "345678912","AM345678");
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_FARMER").orElseThrow(()-> new RuntimeException("Farmer role not found")));
            user.setRoles(roles);
            userRepository.save(user);
            return null;
        });
    }

    @PostConstruct
    public void setup(){
        this.createRolesAndUsers();
    }



}
