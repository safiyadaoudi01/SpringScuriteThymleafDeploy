package com.example.projetsantesecurise.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class PW {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String objectif;
    @Lob
    @Column(name = "docs", columnDefinition = "BLOB")
    private byte[] docs;

    @Transient
    @JsonIgnore
    private MultipartFile docsFile;

    @ManyToOne
    private Tooth tooth;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "pw_group",
            joinColumns = @JoinColumn(name = "pw_id"),
            inverseJoinColumns = @JoinColumn(name = "groupe_id")
    )
    private Set<Groupe> groupes = new HashSet<>();


}
