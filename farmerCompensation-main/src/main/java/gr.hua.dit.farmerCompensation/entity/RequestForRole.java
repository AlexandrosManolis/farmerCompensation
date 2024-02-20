package gr.hua.dit.farmerCompensation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;


@Entity
@Table(name = "request")
public class RequestForRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //status can be only Pending,Approved,or Rejected
    @Check(constraints = "status IN ('Pending', 'Approved', 'Rejected')")
    @Column(name = "status",columnDefinition = "VARCHAR(20) DEFAULT 'Pending'")
    private String status;

    //many to one relationship with user
    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    //many to one relationship with role
    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "role_id")
    private Role role;

    public RequestForRole(String role_status) {
        this.status = role_status;
    }

    public RequestForRole() {
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
                ", roleStatus='" + status + '\'' +
                ", user=" + user +
                ", role=" + role +
                '}';
    }
}
