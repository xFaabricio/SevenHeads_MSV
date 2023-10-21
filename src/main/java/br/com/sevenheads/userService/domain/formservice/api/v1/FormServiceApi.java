package br.com.sevenheads.userService.domain.formservice.api.v1;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.sevenheads.userService.domain.entity.FormService;
import br.com.sevenheads.userService.domain.entity.FormServiceHistory;
import br.com.sevenheads.userService.domain.entity.User;
import br.com.sevenheads.userService.domain.repository.FormServiceHistoryRepository;
import br.com.sevenheads.userService.domain.repository.FormServiceRepository;
import br.com.sevenheads.userService.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FormServiceApi {

	private final FormServiceRepository formServiceRepository;
	
	private final FormServiceHistoryRepository formServiceHistoryRepository;
	
	private final UserRepository userRepository;
	
	public FormService findById(UUID id) {
		Optional<FormService> formService = formServiceRepository.findById(id);
		if(formService.isPresent()) {
			return formService.get();
		}
		return null;
	}
	
	public User findUserByFormService(FormService formService) {
		Optional<User> user = userRepository.findById(formService.getIdUser());
		if(user.isPresent()) {
			return user.get();
		}
		return null;
	}
	
	public FormServiceHistory saveFormServiceHistory(FormServiceHistory formServiceHistory) {
		return formServiceHistoryRepository.save(formServiceHistory);
	}

	public String findRequesterEmail(Map<String, String> data) {
		for (Map.Entry<String, String> entry : data.entrySet()) {
			String keyForm = entry.getKey();
			String value = entry.getValue();

			if (keyForm.equals("e-mail") || keyForm.equals("email") && (value != null && !value.equals(""))) {
				return value;
			}
		}
		return null;
	}

	public boolean alreadySend(String jsonData, FormService formService){
		Optional<List<FormServiceHistory>> formServiceHistory = formServiceHistoryRepository.findFormServiceHistoriesByUuidFormServiceOrderByCreateDateDesc(formService.getId());

		if(formServiceHistory.isPresent()){
			for(FormServiceHistory fsHistory : formServiceHistory.get()){
				if(fsHistory.getMessage().equals(jsonData)){
					return true;
				}
			}
		}
		return false;
	}

}
