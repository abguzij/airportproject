package kg.airport.airportproject.mapper;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static ApplicationUsersEntity mapUserToApplicationUsersEntity(User user) {
        ApplicationUsersEntity applicationUsersEntity = new ApplicationUsersEntity();
        applicationUsersEntity
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .setEnabled(user.isEnabled());

        List<UserRolesEntity> userRolesEntityList = new ArrayList<>();
        for (GrantedAuthority authority : user.getAuthorities()) {
            userRolesEntityList.add((UserRolesEntity) authority);
        }

        applicationUsersEntity.setUserRolesEntityList(userRolesEntityList);
        return applicationUsersEntity;
    }

    public static User mapApplicationUserEntityToUser(ApplicationUsersEntity source) {
        return new User(source.getUsername(), source.getPassword(), source.getAuthorities());
    }
}
