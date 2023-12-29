package com.example.projetsantesecurise.repository;

import com.example.projetsantesecurise.models.Professor;
import com.example.projetsantesecurise.models.User;
import org.springframework.data.repository.CrudRepository;

public interface ProfessorRepository extends CrudRepository<Professor, Long> {
    Professor findByEmail(String email);
}
