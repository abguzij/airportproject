package kg.airport.airportproject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenHandler jwtTokenHandler;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(
            JwtTokenHandler jwtTokenHandler,
            UserDetailsService userDetailsService
    ) {
        this.jwtTokenHandler = jwtTokenHandler;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws ServletException,
            IOException
    {
        String jwtTokenValue = this.parseJwtTokenFromRequest(request);
        if (Objects.nonNull(jwtTokenValue) && this.jwtTokenHandler.validateExistingToken(jwtTokenValue)) {
            String usernameFromToken = this.jwtTokenHandler.getUsernameFromToken(jwtTokenValue);
            UserDetails authenticatedUser = this.userDetailsService.loadUserByUsername(usernameFromToken);

            UsernamePasswordAuthenticationToken basicAuthToken =
                    UsernamePasswordAuthenticationToken.authenticated(
                            authenticatedUser,
                            null,
                            authenticatedUser.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(basicAuthToken);
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwtTokenFromRequest(HttpServletRequest request) {
        final String authorizationHeaderValue = request.getHeader("Authorization");
        if(Objects.nonNull(authorizationHeaderValue) && authorizationHeaderValue.startsWith("Bearer ")) {
            return authorizationHeaderValue.substring(7);
        }
        return null;
    }
}
