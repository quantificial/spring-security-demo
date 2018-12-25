package hello;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Order(0)
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	CustomAuthenticationProvider customAuthenticationProvider;
	
	@Autowired
	HeroAuthenticationProvider heroAuthenticationProvider;
	
	@Autowired
	RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	
	@Autowired
	MySavedRequestAwareAuthenticationSuccessHandler mySavedRequestAwareAuthenticationSuccessHandler;
	
	@Autowired
	MySimpleUrlAuthenticationFailureHandler mySimpleUrlAuthenticationFailureHandler;
	
	
	/**
	 * The custom filter is able to handle JSON call and also Form POST authentication
	 * 
	 * @return
	 * @throws Exception
	 */
	@Bean
	CustomAuthenticationFilter authenticationFilter() throws Exception {
		CustomAuthenticationFilter userAuthenticationFilter = new CustomAuthenticationFilter("/signin");	
		userAuthenticationFilter.setAuthenticationManager(authenticationManager());
		userAuthenticationFilter.setAuthenticationSuccessHandler(mySavedRequestAwareAuthenticationSuccessHandler);
		userAuthenticationFilter.setAuthenticationFailureHandler(mySimpleUrlAuthenticationFailureHandler);
		return userAuthenticationFilter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// only permit access "/: and "/home" page
		http.authorizeRequests().antMatchers("/", "/home", "/login", "/signin").permitAll(); 
		
		http.authorizeRequests().antMatchers("/api/**").authenticated();
																						
		// other request need to authenticated
		http.authorizeRequests().anyRequest().authenticated();
		

		http.logout().logoutUrl("/logout") // specify the logout url

				// implement the logout success handler
				.logoutSuccessHandler((req, res, auth) -> { // Logout handler called after successful logout
					req.getSession().setAttribute("message", "You are logged out successfully.");
					res.sendRedirect("/login"); // Redirect user to login page with message.
				}).permitAll(); // logout is allow to permit all

		http.csrf().disable(); 
		http.cors().disable();
		
		// need to define the custom authentication entry point
		// if return 401 unauthorized to access protected resources
		// and no redirect to the login page
		http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);
		
		http.addFilterAt(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
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