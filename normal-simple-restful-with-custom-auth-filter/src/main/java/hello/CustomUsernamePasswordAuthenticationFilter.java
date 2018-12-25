package hello;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * custom filter based on UsernamePasswordAuthenticationFilter to handle authentication through JSON Post
 * @author Fu
 *
 */
@Slf4j
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private String username;
	private String password;
	
    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String password = null; 

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            password = this.password;
        }else{
            password = super.obtainPassword(request);
        }

        return password;
    }
    
    @Override
    protected String obtainUsername(HttpServletRequest request){
        String username = null;

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            username = this.username;
        }else{
            username = super.obtainUsername(request);
        }

        return username;
    }
    
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
    	
    	log.info("running custom username password authentication filter");
    	
        if ("application/json".equals(request.getHeader("Content-Type"))) {
            try {
                /*
                 * HttpServletRequest can be read only once
                 */
            	
//                StringBuffer sb = new StringBuffer();
//                String line = null;
//
//                BufferedReader reader = request.getReader();
//                while ((line = reader.readLine()) != null){
//                    sb.append(line);
//                }
                
                String loginUserJson = IOUtils.toString(request.getReader());

                //json transformation
                ObjectMapper mapper = new ObjectMapper();
                LoginModel loginRequest = mapper.readValue(loginUserJson, LoginModel.class);

                this.username = loginRequest.getUsername();
                this.password = loginRequest.getPassword();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
        	log.info("not application/json");
        }

        return super.attemptAuthentication(request, response);
    }
    
    

}
