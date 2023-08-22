package kg.airport.airportproject.security;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import org.springframework.http.HttpHeaders;

public interface TestAuthorizationHeaderProvider {
    HttpHeaders getAuthorizationHeaderForUser(ApplicationUsersEntity applicationUsersEntity);
}
