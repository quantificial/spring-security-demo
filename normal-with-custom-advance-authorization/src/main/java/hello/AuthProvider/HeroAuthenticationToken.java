package hello.AuthProvider;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class HeroAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private final String userId;
	private String password;

	public HeroAuthenticationToken(String userId, String password) {
		super(null);
		this.userId = userId;
		this.password = password;
		setAuthenticated(false);
	}

	@Override
	public Object getCredentials() {
		return this.userId;
	}

	@Override
	public Object getPrincipal() {
		return this.password;
	}

}
