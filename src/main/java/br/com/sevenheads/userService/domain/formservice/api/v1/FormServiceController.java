package br.com.sevenheads.userService.domain.formservice.api.v1;

import br.com.sevenheads.userService.config.JwtService;
import br.com.sevenheads.userService.domain.entity.FormService;
import br.com.sevenheads.userService.domain.entity.FormServiceHistory;
import br.com.sevenheads.userService.domain.entity.User;
import br.com.sevenheads.userService.domain.repository.FormServiceHistoryRepository;
import br.com.sevenheads.userService.domain.repository.UserRepository;
import br.com.sevenheads.userService.utility.EmailService;
import br.com.sevenheads.userService.utility.FormatUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/v1/formService")
@RequiredArgsConstructor
public class FormServiceController {

	private final FormServiceApi formServiceApi;
	
	private final EmailService emailService;

	private final TemplateEngine templateEngine;

	private final UserRepository userRepository;

	private final FormServiceHistoryRepository formServiceHistoryRepository;

	private final JwtService jwtService;

	@GetMapping("/history/emails/{uuid}")
	public String getFormServiceHistoryEmails(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("uuid") String uuid) throws JsonProcessingException {
		final String jwtToken;
		final String login;

		if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return "UNAUTHORIZED";
		}

		jwtToken = authorizationHeader.substring(7);
		login = jwtService.extractLogin(jwtToken);

		Optional<User> user = userRepository.findByLogin(login);
		ObjectMapper objectMapper = new ObjectMapper();
		List<String> emails = new ArrayList<>();

		FormService formService = formServiceApi.findById(UUID.fromString(uuid));

		if(user.isPresent() && formService.getIdUser().equals(user.get().getId())){
			Optional<List<FormServiceHistory>> formServiceHistoryList = formServiceHistoryRepository.findFormServiceHistoriesByUuidFormServiceOrderByCreateDateDesc(formService.getId());
			if(formServiceHistoryList.isPresent()){
				for(FormServiceHistory formServiceHistory : formServiceHistoryList.get()){
					emails.addAll(emailService.extractEmails(formServiceHistory.getMessage()));
				}
			}
		}else{
			return "Not found";
		}

		return objectMapper.writeValueAsString(emails);
	}

	@GetMapping("/history/{uuid}")
	public String getFormServiceHistory(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("uuid") String uuid) throws JsonProcessingException {
		final String jwtToken;
		final String login;

		if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return "UNAUTHORIZED";
		}

		jwtToken = authorizationHeader.substring(7);
		login = jwtService.extractLogin(jwtToken);

		Optional<User> user = userRepository.findByLogin(login);
		ObjectMapper objectMapper = new ObjectMapper();

		FormService formService = formServiceApi.findById(UUID.fromString(uuid));

		if(user.isPresent() && formService.getIdUser().equals(user.get().getId())){
			Optional<List<FormServiceHistory>> formServiceHistoryList = formServiceHistoryRepository.findFormServiceHistoriesByUuidFormServiceOrderByCreateDateDesc(formService.getId());
			if(formServiceHistoryList.isPresent()){
				return objectMapper.writeValueAsString(formServiceHistoryList.get());
			}
		}else{
			//FormServiceHistory is not for the same user
			return "Not found";
		}

		return "";
	}

	@PostMapping("/{uuid}")
	public String sendMessages(@PathVariable("uuid") String uuid, @RequestParam Map<String, String> formData){
		FormService formService = formServiceApi.findById(UUID.fromString(uuid));
		Optional<FormServiceHistory> lastFormServiceHistory = formServiceHistoryRepository.findFirstByUuidFormService(formService.getId());
		ObjectMapper objectMapper = new ObjectMapper();
		String formDataJson = "";
		boolean alreadySended = false;
		Context context = new Context();

		String finalResult = "";

		try {
			formDataJson = objectMapper.writeValueAsString(formData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		alreadySended = formServiceApi.alreadySend(formDataJson, formService);

		if(lastFormServiceHistory.isEmpty() || !alreadySended) {
			User user = formServiceApi.findUserByFormService(formService);

			FormServiceHistory formServiceHistory = new FormServiceHistory();
			formServiceHistory.setCreateDate(new Date());
			formServiceHistory.setUuidFormService(formService.getId());

			StringBuilder message = new StringBuilder();
			message.append("Mensagem de nova submissão do formulario para: " + user.getName() + " e-mail: " + user.getEmail());

			String requesterEmail = formServiceApi.findRequesterEmail(formData);
			if(requesterEmail != null && !requesterEmail.isEmpty()){
				message.append(" Encontrado e-mail no formulario: " + requesterEmail);
				formServiceHistory.setSendMessage(true);
			}

			if (formServiceHistory.getSendMessage() == null) {
				formServiceHistory.setSendMessage(false);
			}

			message.append(" Json que será salvo do formulario: " + formDataJson);
			formServiceHistory.setMessage(formDataJson);

			formServiceApi.saveFormServiceHistory(formServiceHistory);

			if (Boolean.TRUE.equals(formServiceHistory.getSendMessage())) {
				emailService.sendEmailFormServiceRequester(requesterEmail);
			}

			if (Boolean.TRUE.equals(formService.getSendMessage())) {
				emailService.sendEmailFormServiceOwner(user.getEmail(), user.getLogin(), formData);
			}

			finalResult = templateEngine.process("success", context);
		}else {
			if (alreadySended){
				finalResult = templateEngine.process("success-already", context);
			}
		}

		try {
			if (formService.getUseCustomHTML() != null && formService.getUseCustomHTML() && FormatUtil.isValidHtml(formService.getCustomHTML())) {
				finalResult = formService.getCustomHTML();
			}

			if (formService.getUseCustomRedirect() != null && formService.getUseCustomRedirect() && FormatUtil.isValidUrl(formService.getCustomRedirect())) {
				context.setVariable("newPage", formService.getCustomRedirect());
				finalResult = templateEngine.process("redirect", context);
			}
		} catch (IllegalStateException e) {
			Logger.getLogger(FormServiceController.class.getName()).log(Level.SEVERE, null, e);
		}


		return finalResult;
	}

	@Hidden
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
