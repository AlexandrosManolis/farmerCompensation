package gr.hua.dit.farmerCompensation.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
//unique constraint
@Entity
@Table( name="users",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "username"),
            @UniqueConstraint(columnNames = "email"),
            @UniqueConstraint(columnNames = "afm"),
            @UniqueConstraint(columnNames = "identity")
        }
)
@JsonIgnoreProperties({"declarations", "requestForRoles"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Email
    @Size(max = 35)
    private String email;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Pattern(regexp = "^.{5,}$", flags = Pattern.Flag.MULTILINE)
    private String password;

    @NotBlank
    @Size(max=30)
    private String full_name;

    @NotBlank
    @Size(max= 20)
    private String address;
    //check for only 9 digits
    @NotBlank
    @Size(min = 9, max = 9, message = "Number should contain exactly 9 digits.")
    @Pattern(regexp = "\\d+", message = "Number should contain only digits.")
    private String afm;

    //identity AA123456
    @NotBlank
    @Size(min=8, max=8)
    @Pattern(regexp = "[A-Z]{2}\\d{6}", flags = Pattern.Flag.MULTILINE)
    private String identity;

    //many to many relationship with role and user create user_role entity
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable( name = "user_role",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns= @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String email, String username, String password, String full_name, String address, String afm, String identity) {
        this.email = email;
        this.username = username;
        this.password = password;
       this.full_name = full_name;
        this.address = address;
        this.afm = afm;
        this.identity = identity;
        this.roles = new HashSet<>();
    }


    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    private List<DeclarationForm> declarations;
    public List<DeclarationForm> getDeclarations() {
        return declarations;
    }


    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    private List<RequestForRole> requestForRoles;
    public List<RequestForRole> getRequestForRoles() {
        return requestForRoles;
    }

    public void setRequests(List<RequestForRole> requestForRoles) {
        this.requestForRoles = requestForRoles;
    }

    public void setDeclarations(List<DeclarationForm> declarations) {
        this.declarations = declarations;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAfm() {
        return afm;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity_id(String identity) {
        this.identity = identity;
    }



    @Override
    public String toString() {
        return full_name;
    }
    public Set<Role> getRoles() {
    return roles;
}

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
