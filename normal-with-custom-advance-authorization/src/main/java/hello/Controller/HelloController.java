package hello.Controller;

import java.security.Principal;
import java.util.Collection;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	// applied the pre authorize and only allow role admin to access this method
	//
	// the PreFilter is used to apply for the collection objects
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping("/add")
	public int addNumber(@RequestParam(value="a") int a, @RequestParam(value="b") int b) {
		return a+b;
	}
	
	/**
	 * get login user detail from security context holder
	 * @return
	 */
	@RequestMapping("/name")
	public String displayName() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName();
	}
	
	@RequestMapping("/roles")
	public Collection displayRoles() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getAuthorities();
	}
	
	/**
	 * get name from the principal injected by spring security
	 * @param principal
	 * @return
	 */
	@RequestMapping("/name2")
	public String displayName2(Principal principal) {
		String name = principal.getName();
		return name;
	}
	
	@RequestMapping("/name3")
	public String displayName3() {
		Object name = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return name.toString();
	}
	
}
