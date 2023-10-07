package com.example.fatec.ninetech.models;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nome;

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

    public LiderDeProjeto findByNome(String novoNome) {
        // TODO Auto-generated method stub
        return null;
    }

    // Construtor sem argumentos
    public LiderDeProjeto() {
    }

    // Construtor que aceita um valor `Number` como argumento
    public LiderDeProjeto(Number liderDeProjetoId) {
        this.id = liderDeProjetoId.longValue();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Use os valores do enum em letras maiúsculas
        if (this.role == UsuarioRole.ENGENHEIRO_CHEFE) {
            return List.of(new SimpleGrantedAuthority("ROLE_ENGENHEIRO_CHEFE"), new SimpleGrantedAuthority("ROLE_LIDER_DE_PROJETO_1"), new SimpleGrantedAuthority("ROLE_LIDER_DE_PROJETO_2"));
        } else if (this.role == UsuarioRole.LIDER_DE_PROJETO_1) {
            return List.of(new SimpleGrantedAuthority("ROLE_LIDER_DE_PROJETO_1"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_LIDER_DE_PROJETO_2"));
        }
    }

    @Override
    public String getUsername() {
        return nome;
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
}
