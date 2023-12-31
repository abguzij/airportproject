package kg.airport.airportproject.mock;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.security.TestCredentialsProvider;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public static ApplicationUsersEntity buildDefaultEngineersEntity() {
        ApplicationUsersEntity engineer = new ApplicationUsersEntity()
                .setUsername(TestCredentialsProvider.DEFAULT_ENGINEERS_USERNAME)
                .setPassword(passwordEncoder.encode(TestCredentialsProvider.DEFAULT_ENGINEERS_RAW_PASSWORD))
                .setFullName("Default Engineer")
                .setId(TestCredentialsProvider.ENGINEERS_DEFAULT_ID)
                .setEnabled(true);
        engineer.getUserRolesEntityList().add(new UserRolesEntity().setId(7L).setRoleTitle("ENGINEER"));
        return engineer;
    }
}
