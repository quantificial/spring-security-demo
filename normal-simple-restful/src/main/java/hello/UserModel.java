package hello;

import lombok.Data;

/**
 * defined the user model to store the user detail and information
 * @author Fu
 *
 */
@Data
public class UserModel {
	
	  private String username;
	  private String password;
	  private String[] roles;

	  public UserModel(String username, String password, String... roles) {
	    this.username = username;
	    this.password = password;
	    this.roles = roles;
	  }
}
