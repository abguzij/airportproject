package kg.airport.airportproject.adapter;

import kg.airport.airportproject.entity.AircraftsEntity;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.mapper.UserMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class InMemoryUserDetailsManagerAdapter implements UserDetailsService {
    private final InMemoryUserDetailsManager inMemoryUserDetailsManager;
    private Map<String, AircraftsEntity> usersServicedAircraftMap;
    private Map<String, Long> userIdsMap;

    public InMemoryUserDetailsManagerAdapter(ApplicationUsersEntity... applicationUsersEntities) {
        this.userIdsMap = this.extractUserIdsFromUsersList(List.of(applicationUsersEntities));
        this.usersServicedAircraftMap =
                this.extractEngineersServicedAircraftsFromUsersList(List.of(applicationUsersEntities));
        this.inMemoryUserDetailsManager = new InMemoryUserDetailsManager(applicationUsersEntities);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = (User) this.inMemoryUserDetailsManager.loadUserByUsername(username);
        ApplicationUsersEntity applicationUser = UserMapper.mapUserToApplicationUsersEntity(user);

        applicationUser.setServicedAircraft(this.usersServicedAircraftMap.get(username));
        applicationUser.setId(this.userIdsMap.get(username));

        return applicationUser;
    }

    public void updateUsersServicedAircraftByUsername(
            String username,
            AircraftsEntity aircraftsEntity
    ) {
        this.usersServicedAircraftMap.put(username, aircraftsEntity);
    }

    private Map<String, AircraftsEntity> extractEngineersServicedAircraftsFromUsersList(
            List<ApplicationUsersEntity> applicationUsersEntityList
    ) {
        return applicationUsersEntityList
                .stream()
                .filter(applicationUsersEntity -> Objects.nonNull(applicationUsersEntity.getServicedAircraft()))
                .collect(
                        Collectors.toMap(
                                ApplicationUsersEntity::getUsername,
                                ApplicationUsersEntity::getServicedAircraft
                        )
                );
    }

    private Map<String, Long> extractUserIdsFromUsersList(List<ApplicationUsersEntity> applicationUsersEntityList) {
        return applicationUsersEntityList
                .stream()
                .filter(applicationUsersEntity -> Objects.nonNull(applicationUsersEntity.getId()))
                .collect(
                        Collectors.toMap(
                                ApplicationUsersEntity::getUsername,
                                ApplicationUsersEntity::getId
                        )
                );
    }
}
