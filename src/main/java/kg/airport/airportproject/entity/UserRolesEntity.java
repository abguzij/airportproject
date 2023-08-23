package kg.airport.airportproject.entity;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "public", name = "user_roles")
public class UserRolesEntity implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "role_title")
    private String roleTitle;

    @ManyToOne
    @JoinColumn(name = "position_id", referencedColumnName = "id")
    private UserPositionsEntity userPositions;
    @ManyToMany(mappedBy = "userRolesEntityList", cascade = CascadeType.MERGE)
    private List<ApplicationUsersEntity> applicationUsersEntityList;

    public UserRolesEntity() {
        this.applicationUsersEntityList = new ArrayList<>();
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + this.getRoleTitle();
    }

    public Long getId() {
        return id;
    }

    public UserRolesEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public UserRolesEntity setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
        return this;
    }

    public UserPositionsEntity getUserPositions() {
        return userPositions;
    }

    public UserRolesEntity setUserPositions(UserPositionsEntity userPositions) {
        this.userPositions = userPositions;
        return this;
    }

    public List<ApplicationUsersEntity> getApplicationUsersEntityList() {
        return applicationUsersEntityList;
    }

    public UserRolesEntity setApplicationUsersEntityList(List<ApplicationUsersEntity> applicationUsersEntityList) {
        this.applicationUsersEntityList = applicationUsersEntityList;
        return this;
    }
}
