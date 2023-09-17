package br.com.sevenheads.userService.domain.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sh_form_service_history")
public class FormServiceHistory implements Serializable {
	
	private static final long serialVersionUID = 7963147802655889486L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_history_generator")
	@SequenceGenerator(name = "form_history_generator", sequenceName = "sh_form_service_history_seq", allocationSize = 1)
	private Long id;

	@Column(name = "form_service_id")
	private UUID uuidFormService;
		
	@Column
	private String message;
	
	@Column
	private Date createDate;

	@Column
	private Boolean sendMessage;	
}
