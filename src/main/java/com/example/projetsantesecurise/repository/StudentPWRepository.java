package com.example.projetsantesecurise.repository;

import com.example.projetsantesecurise.models.Groupe;
import com.example.projetsantesecurise.models.PW;
import com.example.projetsantesecurise.models.StudentPW;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface StudentPWRepository extends CrudRepository<StudentPW, Long> {
    List<StudentPW> findByStudentId(Long id);


}
