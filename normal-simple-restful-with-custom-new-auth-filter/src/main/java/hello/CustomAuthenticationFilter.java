package hello;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.apache.commons.io.IOUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * custom filter based on UsernamePasswordAuthenticationFilter to handle
 * authentication through JSON Post
 * 
 * @author Fu
 *
 */
@Slf4j
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public CustomAuthenticationFilter(String filterProcessesUrl) {
		super(new AntPathRequestMatcher(filterProcessesUrl, RequestMethod.POST.name()));
		
		log.info("RequestMethod.POST.name():" + RequestMethod.POST.name());
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)  throws AuthenticationException {
		
		/**
		 * only allow POST to operate the authentication
		 */
	    if (!request.getMethod().equals(RequestMethod.POST.name())) {
	        throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
	      }
	    
	    Authentication authentication = null;

		log.info("running custom username password authentication filter");

		if ("application/json".equals(request.getHeader("Content-Type"))) {
			try {

				// convert the POST JSON Data to correct JSON object

				String loginUserJson = IOUtils.toString(request.getReader());

				// json transformation
				ObjectMapper mapper = new ObjectMapper();
				LoginModel loginRequest = mapper.readValue(loginUserJson, LoginModel.class);
				
				// create the custom authentication token
				// more information could be passed to the custom authentication token if required... 
				
				CustomAuthenticationToken customAuthenticationToken = new CustomAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword() );
				
				// standard handling to build the authentication details
				// it is more useful when using default filter to provide additional information for the authentication
				// rather than just the username and password in the authentication token
				
				customAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
				
				// Trigger the authentication
				authentication = this.getAuthenticationManager().authenticate(customAuthenticationToken);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.info("not application/json");
			
			response.setStatus(Response.SC_FORBIDDEN);
		}
		
		return authentication;

	}

}
