package com.example.projetsantesecurise.repository;

import com.example.projetsantesecurise.models.Professor;
import com.example.projetsantesecurise.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UtilisateurRepository extends CrudRepository<User, Long> {

    User findByEmail(String email);

    User findByUsername(String currentUsername);
}
