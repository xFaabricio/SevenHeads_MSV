package br.com.sevenheads.userService.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "sh_user")
public class User implements Serializable, UserDetails {
	
	private static final long serialVersionUID = -4587481838374402425L;	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="login")
	private String login;
	
	@Column(name="password")
	private String password;
	
	@Column(name="email")
	private String email;
	
	@Column(name="lastLogin")
	private Date lastLogin;
	
	@Column(name="createDate")
	private Date createDate;
	
	@Column(name="updateDate")
	private Date updateDate;
	
	@Column(name="inactiveDate")
	private Date inactiveDate;
	
	@Column(name="blockedDate")
	private Date blockedDate;
	
	@Column(name="active")
	private Boolean active;
	
	@Column(name="blocked")
	private Boolean blocked;
	
	@Column(name="tryQuantity")
	private Integer tryQuantity;
	
	@Column(name="countryCode")
	private String countryCode;
	
	@Column(name="phoneNumber")
	private String phoneNumber;
	
	@Column(name="codeRecoveryPassword")
	private String codeRecoveryPassword;
	
	@Column(name="firstLogin")
	private Boolean firstLogin;
	
	@Column(name="changePassword")
	private Boolean changePassword;
	
	@Column(name="verified")
	private Boolean verified;
	
	@Column(name="agreeTerms")
	private Boolean agreeTerms;
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private UUID idApi;
	
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "sh_user_roles",
			joinColumns= {@JoinColumn(name="user_id", referencedColumnName = "id")},
			inverseJoinColumns = {@JoinColumn(name="role_id", referencedColumnName = "id")})
	private List<Role> roles;
	
	@Column(name="id_user_update")
	private User updateUser;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {		
		List<SimpleGrantedAuthority> roles = new ArrayList<>();		
		for(Role role : this.roles) {
			roles.add(new SimpleGrantedAuthority(role.getKey()));
		}		
		return roles;
	}

	@Override
	public String getUsername() {
		return login;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !blocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return active;
	}	
	
}
