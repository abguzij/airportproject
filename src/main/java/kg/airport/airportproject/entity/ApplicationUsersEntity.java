package kg.airport.airportproject.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(schema = "public", name = "application_users")
public class ApplicationUsersEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "user_password")
    private String password;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @ManyToOne
    @JoinColumn(name = "position_id", referencedColumnName = "id")
    private UserPositionsEntity userPosition;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "m2m_users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private List<UserRolesEntity> userRolesEntityList;

    @OneToMany(mappedBy = "applicationUsersEntity")
    private List<UserFlightsEntity> userFlightsRegistartionsList;
    @OneToOne(mappedBy = "servicedBy", cascade = CascadeType.MERGE)
    private AircraftsEntity servicedAircraft;

    public ApplicationUsersEntity() {
        this.userRolesEntityList = new ArrayList<>();
    }

    @PrePersist
    private void prePersist() {
        this.isEnabled = Boolean.TRUE;
        this.registeredAt = LocalDateTime.now();
        this.userFlightsRegistartionsList = new ArrayList<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getUserRolesEntityList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

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
        return this.isEnabled;
    }

    public ApplicationUsersEntity setUsername(String username) {
        this.username = username;
        return this;
    }

    public ApplicationUsersEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    public ApplicationUsersEntity setEnabled(Boolean enabled) {
        isEnabled = enabled;
        return this;
    }

    public Long getId() {
        return id;
    }

    public ApplicationUsersEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public ApplicationUsersEntity setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public ApplicationUsersEntity setRegisteredAt(LocalDateTime registered_at) {
        this.registeredAt = registered_at;
        return this;
    }

    public UserPositionsEntity getUserPosition() {
        return userPosition;
    }

    public ApplicationUsersEntity setUserPosition(UserPositionsEntity userPositions) {
        this.userPosition = userPositions;
        return this;
    }

    public List<UserRolesEntity> getUserRolesEntityList() {
        return userRolesEntityList;
    }

    public ApplicationUsersEntity setUserRolesEntityList(List<UserRolesEntity> userRolesEntityList) {
        this.userRolesEntityList = userRolesEntityList;
        return this;
    }

    public List<UserFlightsEntity> getUserFlightsRegistartionsList() {
        return userFlightsRegistartionsList;
    }

    public ApplicationUsersEntity setUserFlightsRegistartionsList(List<UserFlightsEntity> userFlightsRegistartionsList) {
        this.userFlightsRegistartionsList = userFlightsRegistartionsList;
        return this;
    }

    public AircraftsEntity getServicedAircraft() {
        return servicedAircraft;
    }

    public ApplicationUsersEntity setServicedAircraft(AircraftsEntity servicedAircraft) {
        this.servicedAircraft = servicedAircraft;
        return this;
    }
}