package hello;

import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private final String userId;
	private String password;

	public CustomAuthenticationToken(String userId, String password) {
		super(null);
		this.userId = userId;
		this.password = password;
		setAuthenticated(false);
	}
	
	public CustomAuthenticationToken(String userId, String password, List<GrantedAuthority> authority) {
		super(authority);
		this.userId = userId;
		this.password = password;
		//authority.stream().forEach( x-> this.getAuthorities().add(x));
		setAuthenticated(true);
	}	

	@Override
	public Object getCredentials() {
		return this.password;
	}

	@Override
	public Object getPrincipal() {
		return this.userId;
	}

}
