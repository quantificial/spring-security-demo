package hello;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MySimpleUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler{

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		
		super.onAuthenticationFailure(request, response, exception);
		
		log.info("MySimpleUrlAuthenticationFailureHandler....");
		
//		http.formLogin().failureHandler((req, res, exp) -> { 
//		// Failure handler invoked after authentication failure
//		String errMsg = "";
//		if (exp.getClass().isAssignableFrom(BadCredentialsException.class)) {
//			errMsg = "Invalid username or password.";
//		} else {
//			errMsg = "Unknown error - " + exp.getMessage();
//		}
//		req.getSession().setAttribute("message", errMsg);
//		res.sendRedirect("/login"); // Redirect user to login page with error message.
//	});
		
	}
	
}
