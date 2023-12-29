package com.example.projetsantesecurise.repository;

import com.example.projetsantesecurise.models.Groupe;
import com.example.projetsantesecurise.models.PW;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface PWRepository  extends CrudRepository<PW, Long> {
    Set<PW> findByGroupes(Groupe groupe);

}
