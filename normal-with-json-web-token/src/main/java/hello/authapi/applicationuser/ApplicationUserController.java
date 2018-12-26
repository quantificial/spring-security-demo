package hello.authapi.applicationuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class ApplicationUserController {

	@Autowired
    private ApplicationUserRepository applicationUserRepository;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * save the user sign up information, including the user name and encoded password
	 * @param user
	 */
    @PostMapping("/sign-up")
    public void signUp(@RequestBody ApplicationUser user) {
    	
    	// when sign up, encode the password
    	
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        
        log.info("sign up user: " + user.getUsername());
        log.info("password has been encrypted");
        
        applicationUserRepository.save(user);
    }
}