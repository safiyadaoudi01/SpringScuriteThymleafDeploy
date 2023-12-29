package com.example.projetsantesecurise.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class StudentPW {
    @EmbeddedId
    private StudentPWPK id;

    private String time;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Lob
    @Column(name = "imageFront", columnDefinition = "LONGBLOB")
    private byte[] imageFront;

    @Transient
    @JsonIgnore
    private MultipartFile imageFrontFile;
    @Lob
    @Column(name = "imageSide", columnDefinition = "LONGBLOB")
    private byte[] imageSide;

    @Transient
    @JsonIgnore
    private MultipartFile imageSideFile;


    @ManyToOne
    @JoinColumn(name="student_id",referencedColumnName="id",insertable = false,updatable = false)
    private Student student;
    @ManyToOne
    @JoinColumn(name="pw_id",referencedColumnName="id",insertable = false,updatable = false)
    private PW pw;

}
