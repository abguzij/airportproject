package kg.airport.airportproject.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenHandler {
    @Value("${jwt.token.secret}")
    private String secretKey;
    @Value("${jwt.token.expired}")
    private Long jwtTokenLifetime;

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + this.jwtTokenLifetime);
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();

        return Jwts
                .builder()
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .setSubject(authenticatedUser.getUsername())
                .signWith(SignatureAlgorithm.HS512, this.secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts
                .parser()
                .setSigningKey(this.secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateExistingToken(String token) {
        try {
            Jwts.parser().setSigningKey(this.secretKey).parse(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired!");
        } catch (MalformedJwtException e) {
            System.out.println("Invalid token!");
        } catch (SignatureException e) {
            System.out.println("Signature incorrect!");
        } catch (IllegalArgumentException e) {
            System.out.println("Token had to contain claims (payload)!");
        }
        return false;
    }
}
