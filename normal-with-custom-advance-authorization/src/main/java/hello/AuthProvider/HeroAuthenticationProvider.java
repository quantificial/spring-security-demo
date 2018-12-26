package hello.AuthProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import hello.UserModel;
import lombok.extern.slf4j.Slf4j;

/**
 * create an customer auth provider to replace the UserDetailsService which just return the UserDetails
 * @author Fu
 *
 */
@Component
@Slf4j
public class HeroAuthenticationProvider implements AuthenticationProvider {
	
	/**
	 * simulate an API call to get the user detail from database or external system
	 * @param name
	 * @return
	 */
	private UserModel loadExternalUserDetailsApi(String name) {
		
		if (name.equalsIgnoreCase("hero")) {
			
			// $2a$04$tAb.OGi36U/yLEmfO1BQQuiybeY3ryEk1bSmd8TpO1kWwVOM1Tm6q = 123456 after encode
			// $2a$12$UDK4wH58oja2kL8koUypF.AqfEBVge4..eGyC70aqEhwVWhnrMMj6 = abcd@1234 after encode
			return new UserModel(name, "abcd@1234", "ADMIN");
		}
		return null;		
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		
		// this authentication is passed from the UsernamePasswordAuthenticationFilter
		// when we need to implement our custom authentication filter, we need to create a custom authentication token as 
		// the Authentication Instance
		
		// AbstractAuthenticationProcessingFilter.doFilter ->
		// UsernamePasswordAuthenticationFilter.attempAuthentication ->
		// ProviderManager.authenticate
		// HeroAuthenticationProvider.authenticate
		
	    String name = authentication.getName();
	    String password = authentication.getCredentials().toString();
	    
	    log.info("hero authentication");
	    log.info("auth-provider:name:" + name);
	    log.info("auth-provider:password:" + password);
	    
	    UserModel userModel = loadExternalUserDetailsApi(name);
	    
	    // simulate to verify the client credentials is valid or not
	    if(userModel.getPassword().equals(password)) {
	    	
	    	log.info("password correct");
	    	
	    	List<GrantedAuthority> grantedAuths = new ArrayList<>();
	    	Arrays.stream(userModel.getRoles()).forEach(x -> grantedAuths.add(new SimpleGrantedAuthority(x)));
	    	
            return new UsernamePasswordAuthenticationToken(
                    name, password, grantedAuths);
	    }else {
	    	throw new BadCredentialsException("Bad Credentials");
	    }
	    
	    //return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		
		// in this application, UsernamePasswordAuthenticationFilter is used and it will only create the UsernamePasswordAuthenticationToken
		// and pass to the authenticate method and hence this supports method will return false and 
		// this authentication provider will not be called
		
		log.info("auth-provider:hero");
		log.info("auth-provider:supports:"+authentication.toString());
		log.info("auth-provider:supports:verify: it's Hero Authentication Token or not");
		return authentication.equals(
				HeroAuthenticationToken.class);
	}

}
