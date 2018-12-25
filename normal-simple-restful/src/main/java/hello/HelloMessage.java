package hello;

import lombok.Data;

@Data
public class HelloMessage {

	private int id;
	private String message;
	
	
	public HelloMessage(int id, String message) {
		this.id = id;
		this.message = message;
	}
}
