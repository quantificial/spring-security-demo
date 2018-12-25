package hello;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MySavedRequestAwareAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private RequestCache requestCache = new HttpSessionRequestCache();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		
//	      .successHandler((req,res,auth)->{    //Success handler invoked after successful authentication
//	          for (GrantedAuthority authority : auth.getAuthorities()) {
//	             log.info("authority:" + authority.getAuthority());
//	          }
//	          log.info(auth.getName());
//	          res.sendRedirect("/hello"); // Redirect user to index/home page
//	       })
		
		// Success handler invoked after successful authentication
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			log.info("authority:" + authority.getAuthority());
		}
		
		log.info(authentication.getName());

		SavedRequest savedRequest = requestCache.getRequest(request, response);

		if (savedRequest == null) {
			clearAuthenticationAttributes(request);
			return;
		}
		String targetUrlParam = getTargetUrlParameter();
		if (isAlwaysUseDefaultTargetUrl()
				|| (targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
			requestCache.removeRequest(request, response);
			clearAuthenticationAttributes(request);
			return;
		}

		clearAuthenticationAttributes(request);
	}

	public void setRequestCache(RequestCache requestCache) {
		this.requestCache = requestCache;
	}
}