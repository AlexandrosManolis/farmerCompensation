package gr.hua.dit.farmerCompensation.service;

import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private DeclarationService declarationService;

    @Autowired
    private DeclarationRepository declarationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    public InitialDataService(UserRepository userRepository,
                              UserDAO userDAO,
                              RoleRepository roleRepository,
                              DeclarationRepository declarationRepository,
                              DeclarationService declarationService,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userDAO=userDAO;
        this.roleRepository = roleRepository;
        this.declarationRepository = declarationRepository;
        this.declarationService= declarationService;
        this.passwordEncoder = passwordEncoder;
    }
    //get current date
    private Date getCurrentDate() {

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


        try {
            String formattedDate = dateFormat.format(currentDate);
            return dateFormat.parse(formattedDate);
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception as needed
            return null;
        }
    }
    //create users in db if they are not exist
    private void createRolesUsersAndDeclarations() {

        final List<String> rolesToCreate = List.of("ROLE_ADMIN", "ROLE_FARMER", "ROLE_INSPECTOR");
        for (final String roleName : rolesToCreate) {
            roleRepository.findByName(roleName).orElseGet(() -> {
                roleRepository.save(new Role(roleName));
                return null;
            });
        }

        userRepository.findByUsername("admin").orElseGet(()-> {

            User adminUser = new User("admin@example.com","admin", this.passwordEncoder.encode("admin"),"Admin","admin_address 1","123456789","AD123456");
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_ADMIN").orElseThrow(()-> new RuntimeException("Admin role not found")));
            adminUser.setRoles(roles);

            userRepository.save(adminUser);
            return null;
        });

        userRepository.findByUsername("user1").orElseGet(() -> {
            User user = new User("user1@gmail.com", "user1", this.passwordEncoder.encode("user1"),"Giorgos Papadopoulos","Ippokratous 15", "012345678","AM234567");
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_FARMER").orElseThrow(()-> new RuntimeException("Farmer role not found")));
            user.setRoles(roles);
            userRepository.save(user);
            return null;
        });

        userRepository.findByUsername("user2").orElseGet(() -> {
            User user = new User("user2@gmail.com", "user2", this.passwordEncoder.encode("user2"),"Giannis Iliopoulos","Agiou Nikolaou 25", "234567890","AM012345");
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_FARMER").orElseThrow(()-> new RuntimeException("Farmer role not found")));
            roles.add(roleRepository.findByName("ROLE_INSPECTOR").orElseThrow(()-> new RuntimeException("Inspector role not found")));
            user.setRoles(roles);
            userRepository.save(user);
            return null;
        });

        userRepository.findByUsername("user3").orElseGet(() -> {
            User user = new User("user3@gmail.com", "user3", this.passwordEncoder.encode("user3"),"Ioanna Tsigkou","Ermou 36", "345678912","AM345678");
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_FARMER").orElseThrow(()-> new RuntimeException("Farmer role not found")));
            user.setRoles(roles);
            userRepository.save(user);
            return null;
        });

        declarationRepository.findByFieldAddress("Thiseos 56").orElseGet(()->{
            try{
                DeclarationForm declarationForm= new DeclarationForm();

                declarationForm.setAnnualStartProduction(new SimpleDateFormat("dd/MM/yyyy").parse("06/09/2023"));
                declarationForm.setDamageDate(new SimpleDateFormat("dd/MM/yyyy").parse("08/02/2024"));
                declarationForm.setDescription("Field destroyed due to severe weather");
                declarationForm.setFieldAddress("Thiseos 56");
                declarationForm.setFieldSize("189");
                declarationForm.setPlant_production("Wheat,Corn");
                declarationForm.setStatus("Pending");

                declarationForm.setSubmissionDate(getCurrentDate());
                declarationService.saveDeclaration(declarationForm,userDAO.getUserId("user1"));

            }catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
    //when program starts call createRolesUsersAndDeclarations
    @PostConstruct
    public void setup(){
        this.createRolesUsersAndDeclarations();
    }



}
