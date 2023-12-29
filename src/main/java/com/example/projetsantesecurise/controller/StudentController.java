package com.example.projetsantesecurise.controller;

import com.example.projetsantesecurise.models.Groupe;
import com.example.projetsantesecurise.models.Professor;
import com.example.projetsantesecurise.models.Student;
import com.example.projetsantesecurise.models.StudentPW;
import com.example.projetsantesecurise.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/prof")
public class StudentController {

    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentPWRepository studentPWRepository;

//////////////////////////////////studentList.html

    private String encodePhoto(byte[] photo) {
        return (photo != null && photo.length > 0) ? Base64.getEncoder().encodeToString(photo) : "";
    }

    @GetMapping("/student")
    public String showIndex(Model model, Principal principal) {
        List<String> encodedPhotos = new ArrayList<>();
        String email = principal.getName();
        Professor professor = professorRepository.findByEmail(email);
        model.addAttribute("professor",professor);
        model.addAttribute("newStudent",new Student());

        List<Student> students = studentRepository.findByGroupeIn(professor.getGroupes());

        for (Student student : students) {
            encodedPhotos.add(encodePhoto(student.getPhoto()));
        }

        model.addAttribute("encodedPhotos", encodedPhotos);
        model.addAttribute("students",students);
        return "studentList";
    }

    @PostMapping("/student/add")
    public String addStudent(@ModelAttribute Student newStudent, @RequestParam Long groupeId, @RequestParam("photoFile") MultipartFile photoFile) {
        Groupe groupe = groupeRepository.findById(groupeId).orElseThrow();
        newStudent.setGroupe(groupe);
        newStudent.setRole("ROLE_STUDENT");
        String encodedPassword = BCrypt.hashpw(newStudent.getPassword(),BCrypt.gensalt(12));
        newStudent.setPassword(encodedPassword);

        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                newStudent.setPhoto(photoFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }

        studentRepository.save(newStudent);
        return "redirect:/prof/student";
    }



    @GetMapping("/student/findOne/{id}")
    @ResponseBody
    public Student findOne(@PathVariable("id") Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        System.out.println(student);
        return student;
    }


    @PostMapping("/student/update")
    public String updateStudent(@ModelAttribute Student newStudent, @RequestParam Long groupeId, @RequestParam("editphotoFile") MultipartFile editphotoFile) {
        Groupe groupe = groupeRepository.findById(groupeId).orElseThrow();
        newStudent.setGroupe(groupe);
        newStudent.setRole("ROLE_STUDENT");
        String encodedPassword = BCrypt.hashpw(newStudent.getPassword(),BCrypt.gensalt(12));
        newStudent.setPassword(encodedPassword);
        if (editphotoFile != null && !editphotoFile.isEmpty()) {
            try {
                newStudent.setPhoto(editphotoFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        } else {
            // If no new photo is provided, keep the existing photo
            Student existingStudent = studentRepository.findById(newStudent.getId()).orElse(null);
            if (existingStudent != null && existingStudent.getPhoto() != null) {
                newStudent.setPhoto(existingStudent.getPhoto());
            }
        }

        studentRepository.save(newStudent);
        return "redirect:/prof/student";
    }


    @GetMapping("/student/delete/{id}")
    public String deleteStudent(@PathVariable("id") long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));

        long groupId = student.getGroupe().getId();
        studentRepository.delete(student);

        return "redirect:/prof/student";
    }

    @GetMapping("/student/pws/{id}")
    public String showRealisation(@PathVariable("id") long id, Model model, Principal principal){
        List<String> encodedPhotosFront = new ArrayList<>();
        List<String> encodedPhotosSide = new ArrayList<>();
        String email = principal.getName();
        Professor professor = professorRepository.findByEmail(email);
        Student studentById = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
        model.addAttribute("professor",professor);

        List<StudentPW> studentPWList = studentPWRepository.findByStudentId(id);

        for (StudentPW studentpw : studentPWList) {
            encodedPhotosFront.add(encodePhoto(studentpw.getImageFront()));
        }
        for (StudentPW studentpw : studentPWList) {
            encodedPhotosSide.add(encodePhoto(studentpw.getImageSide()));
        }

        model.addAttribute("encodedPhotosFront", encodedPhotosFront);
        model.addAttribute("encodedPhotosSide", encodedPhotosSide);
        model.addAttribute("studentPWList",studentPWList);

        return "realisation";
    }


}
