
package com.example.fatec.ninetech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.fatec.ninetech.models.EngenheiroChefe;

public interface EngenheiroChefeInterface extends JpaRepository<EngenheiroChefe, Long>{
	UserDetails findByLogin(String login);
}
