package kg.airport.airportproject.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(schema = "public", name = "user_positions")
public class UserPositionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "position_title")
    private String positionTitle;

    @OneToMany(mappedBy = "userPositions")
    private List<UserRolesEntity> userRolesEntityList;
    @OneToMany(mappedBy = "userPosition")
    private List<ApplicationUsersEntity> applicationUsersEntityList;

    public UserPositionsEntity() {
    }

    public Long getId() {
        return id;
    }

    public UserPositionsEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public UserPositionsEntity setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
        return this;
    }

    public List<UserRolesEntity> getUserRolesEntityList() {
        return userRolesEntityList;
    }

    public UserPositionsEntity setUserRolesEntityList(List<UserRolesEntity> userRolesEntityList) {
        this.userRolesEntityList = userRolesEntityList;
        return this;
    }

    public List<ApplicationUsersEntity> getApplicationUsersEntityList() {
        return applicationUsersEntityList;
    }

    public UserPositionsEntity setApplicationUsersEntityList(List<ApplicationUsersEntity> applicationUsersEntityList) {
        this.applicationUsersEntityList = applicationUsersEntityList;
        return this;
    }
}
