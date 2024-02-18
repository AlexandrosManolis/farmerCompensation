package gr.hua.dit.farmerCompensation.payload.request;

import gr.hua.dit.farmerCompensation.entity.Role;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;
public class SignupRequest {

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

    @NotBlank
    @Digits(message="Number should contain 9 digits.", fraction = 0, integer = 9)
    private String afm;

    @NotBlank
    @Size(min=8, max=8)
    @Pattern(regexp = "[A-Z]{2}\\d{6}", flags = Pattern.Flag.MULTILINE)
    private String identity;

    private Set<Role> roles;


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

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
