package hello.authapi.security;

public class SecurityConstants {
	
	// secret key to encrypt the header.payload
    public static final String SECRET = "SecretKeyToGenJWTs123456789012345678901234567890:SecretKeyToGenJWTs123456789012345678901234567890:SecretKeyToGenJWTs123456789012345678901234567890";
    
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    
    public static final String TOKEN_PREFIX = "X-Auth-Token ";
    
    public static final String JWT_HEADER_STRING = "Authorization";
    
    public static final String SIGN_UP_URL = "/users/sign-up";
    
    public static final String SIGN_IN_URL = "/users/sign-in";
    
    
}