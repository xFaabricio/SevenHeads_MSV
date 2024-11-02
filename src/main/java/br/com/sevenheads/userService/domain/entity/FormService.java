package br.com.sevenheads.userService.domain.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(name = "sh_form_service")
public class FormService implements Serializable {

	private static final long serialVersionUID = -6280465386650043243L;

	@Id
    @GeneratedValue
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;
	
	@Column
	private String name;
	
	@Column
	private String description;
	
	@Column
	private Boolean sendMessage;
	
	@Column
	private Boolean active;
	
	@Column(name = "sh_user_id", columnDefinition = "NUMERIC", length = 20)	
	private Long idUser;
	
	@Column
	private Date createDate;
	
	@Column
	private Date updateDate;
	
	@Column
	private Date deletedDate;

	@Column
	private Boolean useCustomHTML;

	@Column(columnDefinition = "TEXT")
	private String customHTML;

	@Column
	private Boolean useCustomRedirect;

	@Column
	private String customRedirect;
	
}
