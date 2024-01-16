package gr.hua.dit.farmerCompensation.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gr.hua.dit.farmerCompensation.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String username;

    private String email;
//
//    private String address;
//
//    private String afm;
//
//    private String full_name;
//
//    private String identity_id;


    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;
//    String full_name,String address,String afm,  String identity_id,
    public UserDetailsImpl(Integer id,String email ,String username , String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
//        this.address= address;
//        this.afm=afm;
//        this.full_name=full_name;
//        this.identity_id= identity_id;
        this.authorities = authorities;

    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword(),authorities);
//                user.getFull_name(),
//                user.getAddress(),
//                user.getAfm(),
//                user.getIdentity_id(),

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

//    public String getAddress() {
//        return address;
//    }
//
//    public String getAfm() {
//        return afm;
//    }
//
//    public String getFull_name() {
//        return full_name;
//    }
//
//    public String getIdentity_id() {
//        return identity_id;
//    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
