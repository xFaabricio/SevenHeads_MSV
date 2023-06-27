package br.com.sevenheads.userService.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/server")
@RequiredArgsConstructor
public class ServerController {

	@GetMapping("/up")	
	public String itsWorking() {		
		return "It's Up !";
	}
	
}
