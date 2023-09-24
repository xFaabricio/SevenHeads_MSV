package br.com.sevenheads.userService.utility;

import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final Environment environment;

	private final TemplateEngine templateEngine;

	@SuppressWarnings("deprecation")
	public void sendEmail(String to, String subject, String body, Boolean isHtml) throws MessagingException {
        try {
            String host = environment.getProperty("mail.host");
            String port = environment.getProperty("mail.port");
            String username = environment.getProperty("mail.username");
            String password = environment.getProperty("mail.password");
            String mailFrom = environment.getProperty("mail.from");
            
            SimpleEmail simpleEmail = new SimpleEmail();
            simpleEmail.setHostName(host);
            simpleEmail.setSmtpPort(Integer.parseInt(port));
            simpleEmail.setAuthenticator(new DefaultAuthenticator(username, password));
            simpleEmail.setSSLOnConnect(true);
            simpleEmail.setTLS(true);
            simpleEmail.setStartTLSEnabled(true);
            simpleEmail.setStartTLSRequired(true);
            simpleEmail.setFrom(mailFrom);
			simpleEmail.addTo(to);
			simpleEmail.setSubject(subject);
			simpleEmail.setMsg(body);
			if(isHtml) {
				simpleEmail.setContent(body, "text/html; charset=utf-8");
			}
			
			simpleEmail.send();            
        } catch (Exception e) {
            // trate exceções aqui
        }
    }
	
	public void sendEmailWelcomeDebug(String to, String name) throws MessagingException {
		sendEmailHtml(to, name, true, false, null, true);
	}
	
	public void sendEmailForgotDebug(String to, String name, String newPassword) throws MessagingException {
		sendEmailHtml(to, name, false, true, newPassword, true);
	}
	
	public void sendEmailWelcome(String to, String name) throws MessagingException {
		sendEmailHtml(to, name, true, false, null, false);
	}
	
	public void sendEmailForgot(String to, String name, String newPassword) throws MessagingException {
		sendEmailHtml(to, name, false, true, newPassword, false);
	}
	
	public void sendEmailFormServiceOwner(String to, String login, Map<String, String> formData) {
		try {
			String content = "";
			
			for (Map.Entry<String, String> entry : formData.entrySet()) {
	            String keyForm = entry.getKey();
	            String value = entry.getValue();            	            
                
	            content += "<p style=\"margin: 0; font-size: 14px; text-align: left; mso-line-height-alt: 28px; font-weight: bold;\">";
	            content += keyForm + ":";
	            content += "</p> <p style=\"margin: 0; font-size: 14px; text-align: left; mso-line-height-alt: 28px;\">";
	            content += value;
	            content += "</p> <br>";
	        }
			
			sendEmailHtmlFormService(to, login, content, true, false);
		} catch (EmailException e) {
			e.printStackTrace();
		}
	}
	
	public void sendEmailFormServiceRequester(String to) {
		try {
			sendEmailHtmlFormService(to, null, null, false, true);
		} catch (EmailException e) {
			e.printStackTrace();
		}
	}
	
	public String templateHtmlOwner(String login, String content) {
		Context context = new Context();
		context.setVariable("login", login);
		context.setVariable("content", content);
		return templateEngine.process("owner", context);
	}
	
	public String templateHtmlRequester() {
		Context context = new Context();
		return templateEngine.process("requester", context);
	}
	
	@SuppressWarnings("deprecation")
	public void sendEmailHtmlFormService(String to, String login, String content, boolean owner, boolean requester) throws EmailException {
		
		String host = environment.getProperty("mail.host");
		String port = environment.getProperty("mail.port");
		String username = environment.getProperty("mail.username");
		String password = environment.getProperty("mail.password");
		String mailFrom = environment.getProperty("mail.from");
		
		String body = null;
    	
    	if(owner) {
    		body = templateHtmlOwner(login, content);
    	}
    	
    	if(requester) {
    		body = templateHtmlRequester();
    	}
        
    	if(body != null) {
            SimpleEmail simpleEmail = new SimpleEmail();
            simpleEmail.setHostName(host);
            simpleEmail.setSmtpPort(Integer.parseInt(port));
            simpleEmail.setAuthenticator(new DefaultAuthenticator(username, password));
            simpleEmail.setSSLOnConnect(true);
            simpleEmail.setTLS(true);
            simpleEmail.setStartTLSEnabled(true);
            simpleEmail.setStartTLSRequired(true);
            simpleEmail.setFrom(mailFrom);
			simpleEmail.addTo(to);
			
			if(owner) {
				simpleEmail.setSubject("Chegou uma nova submissão "+login+" !!");
			}
			
			if(requester) {
				simpleEmail.setSubject("Submissão realizada com sucesso !!");
			}
			
			simpleEmail.setMsg(body);			
			simpleEmail.setContent(body, "text/html; charset=utf-8");						
			simpleEmail.send();
    	}
		
	}
	
	@SuppressWarnings("deprecation")
	public void sendEmailHtml(String to, String name, boolean welcome, boolean forgotPassword, String newPassword, boolean debugTest) throws MessagingException {
        try {        	
        	String host;
            String port;
            String username;
            String password;
            String mailFrom;
        	
        	if(!debugTest) {	            	
	            host = environment.getProperty("mail.host");
	            port = environment.getProperty("mail.port");
	            username = environment.getProperty("mail.username");
	            password = environment.getProperty("mail.password");
	            mailFrom = environment.getProperty("mail.from");
        	}else {
        		host = "smtp.zoho.com";
	            port = "587";
	            username = "suporte@sevenheads.com.br";
	            password = "wnrtmihtl0912-A!";
	            mailFrom = "suporte@sevenheads.com.br";
        	}
        	String body = null;
        	
        	if(welcome) {
        		body = templateHtmlWelcome(name);
        	}
        	
        	if(forgotPassword) {
        		body = templateHtmlForgot(name, newPassword);
        	}
            
        	if(body != null) {
	            SimpleEmail simpleEmail = new SimpleEmail();
	            simpleEmail.setHostName(host);
	            simpleEmail.setSmtpPort(Integer.parseInt(port));
	            simpleEmail.setAuthenticator(new DefaultAuthenticator(username, password));
	            simpleEmail.setSSLOnConnect(true);
	            simpleEmail.setTLS(true);
	            simpleEmail.setStartTLSEnabled(true);
	            simpleEmail.setStartTLSRequired(true);
	            simpleEmail.setFrom(mailFrom);
				simpleEmail.addTo(to);
				
				if(welcome) {
					simpleEmail.setSubject("Welcome "+name);
				}
				
				if(forgotPassword) {
					simpleEmail.setSubject("Recovery Account "+name);
				}
				
				simpleEmail.setMsg(body);			
				simpleEmail.setContent(body, "text/html; charset=utf-8");						
				simpleEmail.send();
        	}
        } catch (Exception e) {
            // trate exceções aqui
        }
    }

	public String templateHtmlWelcome(String name) {
		Context context = new Context();
		context.setVariable("name", name);

		return templateEngine.process("welcome", context);
	}
	public String templateHtmlForgot(String name, String newPassword) {
		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("newPassword", newPassword);

		return templateEngine.process("forgot", context);
	}
}
