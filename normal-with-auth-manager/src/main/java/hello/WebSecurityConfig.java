package hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Order(0)
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	/**
	 * add more configuration items in this application
	 * 
	 * 1. username
	 * 2. password
	 * 3. signin endpoint
	 * 4. the success handler
	 * 5. the failure handler
	 * 6. the logout handler 
	 * 
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests().antMatchers("/", "/home").permitAll() // only permit access / and /home page
		
				.anyRequest().authenticated() // other request need to authenticated
				.and()
				.formLogin().loginPage("/login").permitAll() // set default login page url "/login" and the login page is allowed permit all
				
				.loginProcessingUrl("/signin") // changed the default sign page "/login" to become "/signin" for login processing
				
				.usernameParameter("userid") // change the login form username to userid
				.passwordParameter("passwd") // change the login form password to passwd
				
				// implement the success handler
			      .successHandler((req,res,auth)->{    //Success handler invoked after successful authentication
			          for (GrantedAuthority authority : auth.getAuthorities()) {
			             log.info(authority.getAuthority());
			          }
			          log.info(auth.getName());
			          res.sendRedirect("/hello"); // Redirect user to index/home page
			       })
			      
			    // implement the failure handler
			      .failureHandler((req,res,exp)->{  // Failure handler invoked after authentication failure
			          String errMsg="";
			          if(exp.getClass().isAssignableFrom(BadCredentialsException.class)){
			             errMsg="Invalid username or password.";
			          }else{
			             errMsg="Unknown error - "+exp.getMessage();
			          }
			          req.getSession().setAttribute("message", errMsg);
			          res.sendRedirect("/login"); // Redirect user to login page with error message.
			       })
				
				.and().logout().logoutUrl("/logout") // specify the logout url
				
				// implement the logout success handler
			      .logoutSuccessHandler((req,res,auth)->{   // Logout handler called after successful logout 
			          req.getSession().setAttribute("message", "You are logged out successfully.");
			          res.sendRedirect("/login"); // Redirect user to login page with message.
			       })
				.permitAll()
				.and().csrf().disable(); // logout is allow to permit all
	}

	// add the authentication manager
	// and then we could either assign the in-memory userdetail to the auth manager or create from the authen manager
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		//auth.inMemoryAuthentication().withUser("admin").password("admin123").roles("USER");
		auth.userDetailsService(userDetailsService());
	}

	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER")
				.build();
		
		UserDetails admin = User.withDefaultPasswordEncoder().username("admin").password("admin").roles("ADMINx")
				.build();

		return new InMemoryUserDetailsManager(user, admin);
	}
}