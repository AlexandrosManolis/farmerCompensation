package gr.hua.dit.farmerCompensation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;


@Entity
@Table(name = "role_requests")
public class RequestForRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Check(constraints = "role_status IN ('Pending', 'Accepted', 'Rejected')")
    @Column(name = "role_status",columnDefinition = "VARCHAR(20) DEFAULT 'Pending'")
    private String role_status;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "role_id")
    private Role role;

    public RequestForRole(String role_status) {
        this.role_status = role_status;
    }

    public RequestForRole() {
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole_status() {
        return role_status;
    }

    public void setRole_status(String role_status) {
        this.role_status = role_status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "RequestForRole{" +
                "id=" + id +
                ", roleStatus='" + role_status + '\'' +
                ", user=" + user +
                ", role=" + role +
                '}';
    }
}
