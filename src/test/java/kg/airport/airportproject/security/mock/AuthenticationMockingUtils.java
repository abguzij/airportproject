package kg.airport.airportproject.security.mock;

import kg.airport.airportproject.configuration.UserDetailsConfigurationTest;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.security.DefaultCredentialsProvider;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticationMockingUtils {
    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
    public static void mockAuthenticatedEngineer() {
        AuthenticationMockingUtils
                .mockAuthentication(AuthenticationMockingUtils.buildDefaultEngineersEntity());
    }

    public static void mockAuthentication(ApplicationUsersEntity applicationUser) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito
                .when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        Mockito
                .when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .thenReturn(applicationUser);
    }

    public static void mockAuthenticationBeans(
            Authentication authentication,
            SecurityContext securityContext,
            ApplicationUsersEntity applicationUser
    ) {
        Mockito
                .when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        Mockito
                .when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .thenReturn(applicationUser);
    }

    private static ApplicationUsersEntity buildDefaultEngineersEntity() {
        ApplicationUsersEntity engineer = new ApplicationUsersEntity()
                .setUsername(DefaultCredentialsProvider.DEFAULT_ENGINEERS_USERNAME)
                .setPassword(passwordEncoder.encode(DefaultCredentialsProvider.DEFAULT_ENGINEERS_RAW_PASSWORD))
                .setFullName("Default Engineer")
                .setId(DefaultCredentialsProvider.ENGINEERS_DEFAULT_ID)
                .setEnabled(true);
        engineer.getUserRolesEntityList().add(new UserRolesEntity().setId(7L).setRoleTitle("ENGINEER"));
        return engineer;
    }
}
