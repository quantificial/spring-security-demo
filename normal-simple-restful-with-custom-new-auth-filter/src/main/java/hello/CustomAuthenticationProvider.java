package hello;

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
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * create an customer auth provider to replace the UserDetailsService which just return the UserDetails
 * @author Fu
 *
 */
@Component
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	/**
	 * simulate an API call to get the user detail from database or external system
	 * @param name
	 * @return
	 */
	private UserModel loadExternalUserDetailsApi(String name) {
		
		if (name.equalsIgnoreCase("admin")) {
			
			// $2a$04$tAb.OGi36U/yLEmfO1BQQuiybeY3ryEk1bSmd8TpO1kWwVOM1Tm6q = 123456 after encode
			// $2a$12$UDK4wH58oja2kL8koUypF.AqfEBVge4..eGyC70aqEhwVWhnrMMj6 = abcd@1234 after encode
			return new UserModel(name, "abcd@1234", "ADMIN");
		}
		return null;		
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		
		// this authentication is passed from the UsernamePasswordAuthenticationFilter if default form login and default UsernamePassword Filter are used
		// when we need to implement our custom authentication filter, we need to create a custom authentication token as 
		// the Authentication Instance
			
		// AbstractAuthenticationProcessingFilter.doFilter ->
		// UsernamePasswordAuthenticationFilter.attempAuthentication -> (pass the newly created UsernamePasswordAuthenticationToken as Authentication instance)
		// ProviderManager.authenticate  ->  (verify the token is supported or not)
		// CustomAuthenticationProvider.authenticate
		
	    String name = authentication.getName();
	    String password = authentication.getCredentials().toString();
	    
	    // if need to pass more information for the authentication, it is required to implement a customer authentication details source and
	    // build a custom authentication details object 
	    // 1. create CustomWebAuthenticationDetails extends WebAuthenticationDetails and get more information from request object
	    // 2. create CustomAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails>
	    //    and then create the CustomWebAuthenticationDetails object
	    // 3. set in configurate http -> .formLogin -> .authenticationDetailsSource(authenticationDetailsSource)
	    //
	    // however, if we implement the custom authentication filter, we could create the custom authentication token and pass more information into
	    // the token.
	    WebAuthenticationDetails details = (WebAuthenticationDetails)authentication.getDetails();
	    
	    
	    log.info("web authentication details:remoteAddress" + details.getRemoteAddress());
	    log.info("web authentication details:sessionid:"+details.getSessionId());
	    
	    log.info("custom authentication");
	    log.info("auth-provider:name:" + name);
	    log.info("auth-provider:password:" + password);
	    
	    UserModel userModel = loadExternalUserDetailsApi(name);
	    
	    if(userModel == null) throw new BadCredentialsException("Bad Credentials");
	    
	    // simulate to verify the client credentials is valid or not
	    if(userModel.getPassword().equals(password)) {
	    	
	    	log.info("password correct");
	    	
	    	List<GrantedAuthority> grantedAuths = new ArrayList<>();
	    	Arrays.stream(userModel.getRoles()).forEach(x -> grantedAuths.add(new SimpleGrantedAuthority(x)));
	    	
	    	
            //return new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
	    	
	    	// return the authentication token to indicate the authentication is correct
	    	return new CustomAuthenticationToken(name, password, grantedAuths);
	    	
	    }else {
	    	throw new BadCredentialsException("Bad Credentials");
	    }
	    
	    //return null;
	}

	/**
	 * it is important that the the provider is to support CustomAuthenticationProvider instead of the UsernamePasswordAuthentication
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		
		log.info("auth-provider:supports:"+authentication.toString());
		log.info("auth-provider:supports:verify: it's UsernamePasswordAuthenticationToken or not");
		return authentication.equals(
		          CustomAuthenticationToken.class);
	}

}
