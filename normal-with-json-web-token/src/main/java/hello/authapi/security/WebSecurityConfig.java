package hello.authapi.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static hello.authapi.security.SecurityConstants.SIGN_UP_URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * primary configuration for the spring security
 *
 */
@Configuration
@EnableWebSecurity // custom configuration and disable the default setting and need to extend
					// WebSecurityConfigurerAdapter
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	/**
	 * 
	 *  JWT Token Format: header.payload.signature
	 *  
	 *  header: base64 encoded and contain the encrypted information
	 *  payload: base64 encoded with the 'Claim', such as the username and it's just a map structure
	 *  
	 *  signature: encrypted of 'header.payload.signature'
	 * 
	 *  
	 * 
	 */
	

	// request layer configuration - HttpSecurity
	//
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors().and().csrf().disable();

		http.authorizeRequests()
			// URL: /users/sign-up
			.antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll() // permit only sign up URL
			.antMatchers(HttpMethod.POST, "/").permitAll()
			.antMatchers(HttpMethod.GET, "/").permitAll()
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			.antMatchers("/dba/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DBA')")
			.anyRequest().authenticated();
		
		// authentication manager builder is already configured in method configure
		// below
		// and used to create the authentication manager
		http.addFilter(new JWTAuthenticationFilter(authenticationManager()))
			.addFilter(new JWTAuthorizationFilter(authenticationManager()));
			// this disables session creation on Spring Security
		
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		//http.formLogin().loginPage(SecurityConstants.SIGN_IN_URL).permitAll();
		
		//http.formLogin()
		//	.loginPage("/users/login")
		//	// .failureUrl("/login?error")
		//	.usernameParameter("loginId").passwordParameter("passwd").permitAll() // for custom login page
		//;

	}

	// Authentication layer configuration - AuthenticationManagerBuilder
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {

		// 1. set the user details service which is used to return the user detail with
		// name and the encoded password
		// 2. tell the user details service that which password encoder is used

		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

	// use in memory authentication for testing
	// for production, it's required to implement custom UserDetailsService
	// or custom Authentication Provider
//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication().withUser("test").password("123456").roles("USER");
//		auth.inMemoryAuthentication().withUser("admin").password("123456").roles("ADMIN");
//		auth.inMemoryAuthentication().withUser("dba").password("123456").roles("DBA");
//	}

	// allow CORS support and permit any requests from "/**"
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return source;
	}
}