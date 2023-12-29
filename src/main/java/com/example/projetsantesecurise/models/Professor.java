package com.example.projetsantesecurise.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Professor extends User {
    private String grade;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
    @OneToMany(mappedBy = "professor", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Groupe> groupes;
    @Lob
    @Column(name = "photo", columnDefinition = "LONGBLOB")
    private byte[] photo;

    @Transient
    @JsonIgnore
    private MultipartFile photoFile;



    public List<Groupe> getGroupes() {
        return groupes;
    }

    public void setGroupes(List<Groupe> groupes) {
        this.groupes = groupes;
    }

    @Override
    public String toString() {
        return "Professor{" +
                "id=" + getId() +
                ", name='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", grade='" + grade + '\'' +
                '}';
    }


}
