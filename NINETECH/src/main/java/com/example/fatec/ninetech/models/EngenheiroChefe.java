package com.example.fatec.ninetech.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.fatec.ninetech.config.UsuarioRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "engenheiro_chefe")
public class EngenheiroChefe implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7084023030676554939L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String nome;
	
	@Column
	private String login;
	
	@Column
	private String senha;
	
	@Column
	private UsuarioRole role;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public void setRole(UsuarioRole role) {
		this.role = role;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    // Use os valores do enum em letras maiúsculas
	    List<GrantedAuthority> authorities = new ArrayList<>();

	    if (this.role == UsuarioRole.ENGENHEIRO_CHEFE) {
	        authorities.add(new SimpleGrantedAuthority("ROLE_ENGENHEIRO_CHEFE"));
	    }

	    // Percorre todos os líderes de projeto
	    for (UsuarioRole liderDeProjeto : UsuarioRole.values()) {
	        if (liderDeProjeto.name().startsWith("LIDER_DE_PROJETO")) {
	            authorities.add(new SimpleGrantedAuthority("ROLE_" + liderDeProjeto.name()));
	        }
	    }

	    return authorities;
	}
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return senha;
	}
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return login;
	}
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	public String findNomeById(Long id) {
		// TODO Auto-generated method stub
		return nome;
	}

}
