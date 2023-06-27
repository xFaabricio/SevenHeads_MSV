package br.com.sevenheads.userService.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
	
	private String name;
	
	private String login;
	
	private String password;

	private String email;	
	
}
