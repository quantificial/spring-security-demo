package hello;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

	
	/**
	 * this is the protected API
	 * @return
	 */
	@RequestMapping("/sayhello")
	public List<HelloMessage> sayHello() {
		
		HelloMessage a = new HelloMessage(1, "this is testing");
		HelloMessage b = new HelloMessage(2, "hello world!");
		
		return Arrays.asList(a,b);
	}
	
}
