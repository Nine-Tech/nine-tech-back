package com.example.fatec.ninetech.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.example.fatec.ninetech.config.UsuarioRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "lider_de_projeto")

public class LiderDeProjeto implements UserDetails {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7356186984914687774L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nome;
    
    @Column
    private String login;

    @Column
    private String senha;

    @Enumerated(EnumType.STRING) // Especifique o tipo de enumeração para uso com strings
    @Column
    private UsuarioRole role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioRole getRole() {
        return role;
    }

    public void setRole(UsuarioRole role) {
        this.role = role;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getLogin() {
        return login;
    }
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public LiderDeProjeto(String login, String nome, String senha, UsuarioRole role) {
		this.login = login;
		this.nome = nome;
		this.senha = senha;
		this.role = role;
	}

    public LiderDeProjeto findByNome(String novoNome) {
        // TODO Auto-generated method stub
        return null;
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
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return senha;
    }

	public String findNomeById(Long id) {
		// TODO Auto-generated method stub
		return nome;
	}

	public String getSenhaAtual() {
	    String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt(10));
	    return senhaCriptografada;
	}

}
