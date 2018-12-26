package hello.ApplicationEvent;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * application event listener than listen to the event of ContextRefreshedEvent
 * @author Fu
 *
 */

@Slf4j
@Component
public class ApplicationStartListener implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		log.info("the application name is  " + event.getApplicationContext().getApplicationName());
		
	}

}
