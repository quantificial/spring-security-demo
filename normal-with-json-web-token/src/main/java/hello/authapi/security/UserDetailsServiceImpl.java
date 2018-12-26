package hello.authapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import hello.authapi.applicationuser.ApplicationUser;
import hello.authapi.applicationuser.ApplicationUserRepository;

import static java.util.Collections.emptyList;

/**
 * implemented the spring security user details service
 * 
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        
        if (applicationUser == null) {
            throw new UsernameNotFoundException(username);
        }
        
        // return the spring security user object
        return new User(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
        
        
        /**
         * for the authority setting, it's better to inject the RoleService
         * and then use the id to get the roles list 
         * 
         *     @Autowired    		   
    		   private RoleService roleService;    		       		   
    		   roleService.getRoles(login.getId()).forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getName())));

         */
        
    }
    
//    protected List<UserDetails> loadUsersByUsername(String username) {
//    	return getJdbcTemplate().query(usersByUsernameQuery, new String[] {username}, new RowMapper<UserDetails>() {
//    	  public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
//    		String username = rs.getString(1);
//    		String password = rs.getString(2);
//    		boolean enabled = rs.getBoolean(3);
//    		return new User(username, password, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES);
//    	  }
//
//      });    
    
}