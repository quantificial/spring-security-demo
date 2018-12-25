package hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * core spring security setting
 * 
 * @author Fu
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	/**
	 * configure the authorization and basic spring security setting to use the form login
	 * 
	 */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/home").permitAll() // only permit access / and /home page
                .anyRequest().authenticated() // other request need to authenticated
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()  // login page is allowed permit all
                .and()
            .logout()
                .permitAll();
    }

    /**
     * spring security will handle the username and password verification automatically
     * as the InMemoryUserDetailsManager has been used.
     * 
     * the key is to return the UserDetailsService which contains the method loadUserByUsername to retrieve the UserDetails object for
     * authentication purpose
     * 
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
             User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}