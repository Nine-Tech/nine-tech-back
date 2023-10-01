package com.example.fatec.ninetech.helpers;

import com.example.fatec.ninetech.config.UsuarioRole;

public record RegistroDTOServico(String login, String password, UsuarioRole role) {

}
