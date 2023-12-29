package com.example.projetsantesecurise.repository;

import com.example.projetsantesecurise.models.Groupe;
import com.example.projetsantesecurise.models.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentRepository extends CrudRepository<Student, Long> {
    List<Student> findByGroupeIn(List<Groupe> groupes);
}
