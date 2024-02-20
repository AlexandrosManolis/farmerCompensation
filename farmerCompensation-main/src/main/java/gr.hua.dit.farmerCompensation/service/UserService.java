package gr.hua.dit.farmerCompensation.service;

import gr.hua.dit.farmerCompensation.dao.UserDAO;
import gr.hua.dit.farmerCompensation.entity.Role;
import gr.hua.dit.farmerCompensation.entity.User;
import gr.hua.dit.farmerCompensation.repository.RoleRepository;
import gr.hua.dit.farmerCompensation.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void saveUser(User user) {
//        String passwd= user.getPassword();
//        String encodedPasswod = passwordEncoder.encode(passwd);
//        user.setPassword(encodedPasswod);

//        Role role = roleRepository.findByName("ROLE_FARMER")
//                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//        Set<Role> roles = new HashSet<>();
//        roles.add(role);
//        user.setRoles(roles);

        userRepository.save(user);
    }

    //update user
    @Transactional
    public Integer updateUser(User user) {
        user = userRepository.save(user);
        return user.getId();
    }

    //load user via username
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }

    //get users
    @Transactional
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    //get user with a specific id
    public Object getUser(Integer userId) {
        return userRepository.findById(userId).get();
    }

    //get user profile
    public User getUserProfile(Integer user_id) {
        return userDAO.getUserProfile(user_id);
    }

    //get user's role
    public String getUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getAuthorities() != null) {
            if (authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
                return "ROLE_ADMIN";
            } else if (authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_INSPECTOR"))) {
                return "ROLE_INSPECTOR";
            } else if (authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_FARMER"))) {
                return "ROLE_FARMER";
            }
        }
        // Default role if no matching role is found
        return "ROLE_USER";
    }


}
