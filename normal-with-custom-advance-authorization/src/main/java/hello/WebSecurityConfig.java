package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

import hello.AuthProvider.CustomAuthenticationProvider;
import hello.AuthProvider.HeroAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Order(0)
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true) // need to enable the global method security
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	CustomAuthenticationProvider customAuthenticationProvider;
	
	@Autowired
	HeroAuthenticationProvider heroAuthenticationProvider;
	
	// used to remove the role prefix
//	@Override
//	public void configure(WebSecurity web) throws Exception {
//	    web.expressionHandler(new DefaultWebSecurityExpressionHandler() {
//	        @Override
//	        protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, FilterInvocation fi) {
//	            WebSecurityExpressionRoot root = (WebSecurityExpressionRoot) super.createSecurityExpressionRoot(authentication, fi);
//	            root.setDefaultRolePrefix(""); //remove the prefix ROLE_
//	            return root;
//	        }
//	    });
//	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// authorization
		http.authorizeRequests().antMatchers("/", "/home","/login","/signin").permitAll(); // only permit access / and /home page
		http.authorizeRequests().antMatchers("/admin/**").hasAnyRole("ADMIN"); // allow admin to access only
		//http.authorizeRequests().antMatchers("/admin").hasAnyRole("ADMIN"); // allow admin to access only
		http.authorizeRequests().anyRequest().authenticated(); // other request need to authenticated
		
		// basic form login setting
		http.formLogin().loginPage("/login").permitAll(); // set default login page url "/login" and the login page is allowed permit all
		http.formLogin().loginProcessingUrl("/signin"); // changed the default sign page "/login" to become "/signin" for login processing
		http.formLogin().usernameParameter("userid"); // change the login form username to userid
		http.formLogin().passwordParameter("passwd"); // change the login form password to passwd
		
		// implement the success handler
		http.formLogin().successHandler((req,res,auth)->{    //Success handler invoked after successful authentication
	          for (GrantedAuthority authority : auth.getAuthorities()) {
		             log.info("authority:" + authority.getAuthority());
		          }
		          log.info(auth.getName());
		          res.sendRedirect("/hello"); // Redirect user to index/home page
		       });
			
		 // implement the failure handler
		http.formLogin().failureHandler((req,res,exp)->{  // Failure handler invoked after authentication failure
	          String errMsg="";
	          if(exp.getClass().isAssignableFrom(BadCredentialsException.class)){
	             errMsg="Invalid username or password.";
	          }else{
	             errMsg="Unknown error - "+exp.getMessage();
	          }
	          req.getSession().setAttribute("message", errMsg);
	          res.sendRedirect("/login"); // Redirect user to login page with error message.
	       });
		
		
		// logout setting
		http.logout().logoutUrl("/logout"); // specify the logout url
		http.logout().logoutSuccessHandler((req,res,auth)->{   // Logout handler called after successful logout 
	          req.getSession().setAttribute("message", "You are logged out successfully.");
	          res.sendRedirect("/login"); // Redirect user to login page with message.
	       });
		http.logout().permitAll();
		http.logout().deleteCookies("JSESSIONID"); 
				
		http.csrf();
		http.cors();
	}

	// configure the Authentication Manager
	// add the authentication providers to the Manager
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {


		// custom authentication provider is used
		auth.authenticationProvider(this.customAuthenticationProvider);
		
		// supply another authentication provider
		// but the 
		auth.authenticationProvider(this.heroAuthenticationProvider);
	}
	
	
}