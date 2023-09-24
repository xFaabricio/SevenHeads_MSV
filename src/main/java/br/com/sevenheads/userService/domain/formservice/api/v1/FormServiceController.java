package br.com.sevenheads.userService.domain.formservice.api.v1;

import br.com.sevenheads.userService.domain.entity.FormService;
import br.com.sevenheads.userService.domain.entity.FormServiceHistory;
import br.com.sevenheads.userService.domain.entity.User;
import br.com.sevenheads.userService.utility.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1/formService")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FormServiceController {

	private final FormServiceApi formServiceApi;
	
	private final EmailService emailService;

	private final TemplateEngine templateEngine;

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

		Context context = new Context();

		return templateEngine.process("success", context);
	}

	@GetMapping("/test/{key}")
	public String test(@PathVariable("key") String key) {
		Context context = new Context();

        switch (key) {
            case "templateForgot" -> {
                context.setVariable("name", "Fabricio");
                context.setVariable("newPassword", "Pass123");
                return templateEngine.process("forgot", context);
            }
            case "templateOwner" -> {
				String content = getContent();
				context.setVariable("login", "MASTER");
                context.setVariable("content", content);
                return templateEngine.process("owner", context);
            }
            case "templateRequester" -> {
                return templateEngine.process("requester", context);
            }
            case "templateWelcome" -> {
                context.setVariable("name", "Oliveira");
                return templateEngine.process("welcome", context);
            }
			default -> {
				return templateEngine.process("success", context);
			}
        }
	}

	private static String getContent() {
		String content = "";
		content += "<p style=\"margin: 0; font-size: 14px; text-align: left; mso-line-height-alt: 28px; font-weight: bold;\">";
		content += "Name" + ":";
		content += "</p> <p style=\"margin: 0; font-size: 14px; text-align: left; mso-line-height-alt: 28px;\">";
		content += "Fabrício";
		content += "</p> <br>";
		content += "<p style=\"margin: 0; font-size: 14px; text-align: left; mso-line-height-alt: 28px; font-weight: bold;\">";
		content += "keyForm" + ":";
		content += "</p> <p style=\"margin: 0; font-size: 14px; text-align: left; mso-line-height-alt: 28px;\">";
		content += "value";
		content += "</p> <br>";
		return content;
	}

}
