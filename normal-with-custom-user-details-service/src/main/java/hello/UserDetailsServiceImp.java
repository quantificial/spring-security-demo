package hello;

import java.util.stream.Stream;

import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDetailsServiceImp implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		log.info("start executing loadUserByUsername....");

		/*
		 * Here we are using dummy data, you need to load user data from database or
		 * other third party application
		 * 
		 * such as API, or using DAO to get user information from database
		 */
		UserModel user = findUserbyUername(username);
		
		log.info("finding user information: " + username);
		

		// user builder is used to build the UserDetails
		UserBuilder builder = null;
		
		if (user != null) {
			builder = org.springframework.security.core.userdetails.User.withUsername(username);
			//builder.password(new BCryptPasswordEncoder().encode(user.getPassword()));
			builder.password(user.getPassword()); // the password from the "database" is encoded
			builder.roles(user.getRoles());
		} else {
			throw new UsernameNotFoundException("User not found.");
		}

		return builder.build();
	}

	private UserModel findUserbyUername(String username) {
		if (username.equalsIgnoreCase("admin")) {
			
			// simulate the password has been encoded and stored in the database 
			
			// $2a$04$tAb.OGi36U/yLEmfO1BQQuiybeY3ryEk1bSmd8TpO1kWwVOM1Tm6q = 123456 after encode
			// $2a$12$UDK4wH58oja2kL8koUypF.AqfEBVge4..eGyC70aqEhwVWhnrMMj6 = abcd@1234 after encode
			return new UserModel(username, "$2a$12$UDK4wH58oja2kL8koUypF.AqfEBVge4..eGyC70aqEhwVWhnrMMj6", "ADMIN");
		}
		return null;
	}

}
