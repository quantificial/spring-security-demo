package hello.authapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import hello.authapi.applicationuser.ApplicationUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static hello.authapi.security.SecurityConstants.EXPIRATION_TIME;
import static hello.authapi.security.SecurityConstants.JWT_HEADER_STRING;
import static hello.authapi.security.SecurityConstants.SECRET;
import static hello.authapi.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * JWT Authentication Filter is actually used to get the JWT Token only
 * @author Fu
 *
 */
@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
    private AuthenticationManager authenticationManager;

    // need to set the authentication manager
    // need to set the sign in processing url
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.setFilterProcessesUrl(SecurityConstants.SIGN_IN_URL);
    }
    

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
    	
    	log.info(">>>>>>>>>>>>>> attemptAuthentication");
    	
        try {
        	// convert post data to JSON object
            ApplicationUser data = new ObjectMapper().readValue(req.getInputStream(), ApplicationUser.class);
            
            log.info(data.toString());
            
            // since the filter is inherited from the UsernamePasswordAuthenticationFilter
            // the UsernamePasswordAuthenticationToken is re-used for the authentication
            // before authentication, we don't know the authority, so pass null or empty collection
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword(), new ArrayList<>());

            // search from the user detail and validate the input
            return authenticationManager.authenticate(token);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * if authentication success, need to return the JWTS
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        String token = Jwts.builder()
                .setSubject(((User) auth.getPrincipal()).getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
                .compact();
        

        res.addHeader(JWT_HEADER_STRING, TOKEN_PREFIX + token);
    }
}