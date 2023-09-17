package br.com.sevenheads.userService.domain.formservice.api.v1;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.sevenheads.userService.domain.entity.FormService;
import br.com.sevenheads.userService.domain.entity.FormServiceHistory;
import br.com.sevenheads.userService.domain.entity.User;
import br.com.sevenheads.userService.utility.EmailService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/v1/formService")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FormServiceController {

	private final FormServiceApi formServiceApi;
	
	private final Environment environment;
	
	private final EmailService emailService;
	
	@PostMapping("/{uuid}")
	public String sendMessages(@PathVariable("uuid") String uuid, @RequestParam Map<String, String> formData){
		FormService formService = formServiceApi.findById(UUID.fromString(uuid));
		
		User user = formServiceApi.findUserByFormService(formService);
		
		FormServiceHistory formServiceHistory = new FormServiceHistory();
		formServiceHistory.setCreateDate(new Date());
		formServiceHistory.setUuidFormService(formService.getId());
		
		StringBuilder message = new StringBuilder();
		message.append("Mensagem de nova submissão do formulario para: " + user.getName() + " e-mail: " + user.getEmail());
		
		String requesterEmail = "";		
		for (Map.Entry<String, String> entry : formData.entrySet()) {
            String keyForm = entry.getKey();
            String value = entry.getValue();            
            
            if(keyForm.equals("e-mail") || keyForm.equals("email")) {
            	if(value != null && !value.equals("")) {
	            	message.append(" Encontrado e-mail no formulario: " + value);
	            	formServiceHistory.setSendMessage(true);
	            	requesterEmail = value;
            	}
            }                       
        }
		
		if(formServiceHistory.getSendMessage() == null) {
			formServiceHistory.setSendMessage(false);
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String formDataJson = objectMapper.writeValueAsString(formData);
			message.append(" Json que será salvo do formulario: " + formDataJson);
			formServiceHistory.setMessage(formDataJson);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		formServiceApi.saveFormServiceHistory(formServiceHistory);
		
		if(Boolean.TRUE.equals(formServiceHistory.getSendMessage())) {
			emailService.sendEmailFormServiceRequester(requesterEmail);
		}
		
		if(Boolean.TRUE.equals(formService.getSendMessage())) {
			emailService.sendEmailFormServiceOwner(user.getEmail(), user.getLogin(), formData);
		}
		
		return environment.getProperty("successTemplate");
	}	
}
