package com.example.projetsantesecurise.controller;

import com.example.projetsantesecurise.models.Groupe;
import com.example.projetsantesecurise.models.Professor;
import com.example.projetsantesecurise.models.Student;
import com.example.projetsantesecurise.models.User;
import com.example.projetsantesecurise.repository.GroupeRepository;
import com.example.projetsantesecurise.repository.ProfessorRepository;
import com.example.projetsantesecurise.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller

public class ProfessorController {
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private GroupeRepository groupeRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    private String encodePhoto(byte[] photo) {
        return (photo != null && photo.length > 0) ? Base64.getEncoder().encodeToString(photo) : "";
    }

    // Affiche la liste des professeurs
    @GetMapping("/professors")
    public String listProfessors(Model model, Principal principal) {
        List<String> encodedPhotos = new ArrayList<>();
        String email = principal.getName();
        User user = utilisateurRepository.findByEmail(email);
        List<Professor> professors = (List<Professor>) professorRepository.findAll();

        for (Professor professor : professors) {
            encodedPhotos.add(encodePhoto(professor.getPhoto()));
        }

        model.addAttribute("encodedPhotos", encodedPhotos);
        model.addAttribute("user", user);
        model.addAttribute("professors", professors);
        model.addAttribute("professor", new Professor());
        return "listProf";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("professor", new Professor());
        return "add";
    }
    @GetMapping("/prof")
    public String showprof(Model model) {
        return "redirect:/prof/student";
    }

    // Ajoute un professeur
    @PostMapping("/add")
    public String addProfessor(@ModelAttribute Professor professor, @RequestParam("photoFile") MultipartFile photoFile) {
        professor.setRole("ROLE_PROF");
        String encodedPassword = BCrypt.hashpw(professor.getPassword(),BCrypt.gensalt(12));
        professor.setPassword(encodedPassword);
        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                professor.setPhoto(photoFile.getBytes());

            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }
        professorRepository.save(professor);
        return "redirect:/professors";
    }
    @GetMapping("/professors/edit/{id}")
    public String showEditProfessorForm(@PathVariable long id, Model model) {
        Optional<Professor> optionalProfessor = professorRepository.findById(id);

        if (optionalProfessor.isPresent()) {
            model.addAttribute("professor", optionalProfessor.get());
            return "editProfessor";
        } else {
            // Gérer le cas où le professeur n'est pas trouvé
            return "redirect:/professors";
        }
    }



    // Supprime un professeur
    @GetMapping("/professors/delete/{id}")
    public String deleteProfessor(@PathVariable long id) {
        Optional<Professor> professorOptional = professorRepository.findById(id);

        if (professorOptional.isPresent()) {
            Professor professor = professorOptional.get();

            // Retirer l'enseignant de tous les groupes associés
            for (Groupe groupe : professor.getGroupes()) {
                groupe.setProfessor(null);
            }

            // Supprimer l'enseignant
            professorRepository.delete(professor);
        }
        return "redirect:/professors";
    }
    @PostMapping("/professors/edit/{id}")
    public String updateProfessor(@PathVariable long id, @ModelAttribute Professor updatedProfessor) {
        Optional<Professor> optionalProfessor = professorRepository.findById(id);

        if (optionalProfessor.isPresent()) {
            Professor existingProfessor = optionalProfessor.get();
            existingProfessor.setEmail(updatedProfessor.getEmail());
            existingProfessor.setRole("ROLE_PROF");
            existingProfessor.setUsername(updatedProfessor.getUsername());
            existingProfessor.setPassword(updatedProfessor.getPassword());
            existingProfessor.setGrade(updatedProfessor.getGrade());

            professorRepository.save(existingProfessor);
        }

        return "redirect:/professors";
    }
    @PostMapping("/update")
    public String updateprof(@ModelAttribute Professor professor, @RequestParam("editphotoFile") MultipartFile editphotoFile) {
        professor.setRole("ROLE_PROF");
        if (editphotoFile != null && !editphotoFile.isEmpty()) {
            try {
                professor.setPhoto(editphotoFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        } else {
            // If no new photo is provided, keep the existing photo
            Professor existingProfessor = professorRepository.findById(professor.getId()).orElse(null);
            if (existingProfessor != null && existingProfessor.getPhoto() != null) {
                professor.setPhoto(existingProfessor.getPhoto());
            }
        }
        professorRepository.save(professor);
        return "redirect:/professors";
    }
    @PostMapping("/profile/update")
    public String updateprofile(@ModelAttribute Professor professor, @RequestParam("editphotoFile") MultipartFile editphotoFile,Model model) {
        professor.setRole("ROLE_PROF");
        if (editphotoFile != null && !editphotoFile.isEmpty()) {
            try {
                professor.setPhoto(editphotoFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        } else {
            // If no new photo is provided, keep the existing photo
            Professor existingProfessor = professorRepository.findById(professor.getId()).orElse(null);
            if (existingProfessor != null && existingProfessor.getPhoto() != null) {
                professor.setPhoto(existingProfessor.getPhoto());
            }
        }
        professorRepository.save(professor);
        model.addAttribute("encodedPhoto", encodePhoto(professor.getPhoto()));

        return "profile";
    }
    @GetMapping("/findOne/{id}")
    @ResponseBody
    public Professor findOne(@PathVariable("id") Long id) {
        Professor professor = professorRepository.findById(id).orElse(null);

        return professor;
    }


    @GetMapping("/prof/profile/{id}")
    public String changeProfile(@PathVariable("id") Long id,Model model) {

        Professor professor = professorRepository.findById(id).orElse(null);
        model.addAttribute("professor", professor);
        model.addAttribute("encodedPhoto", encodePhoto(professor.getPhoto()));

        return "profile";
    }


}
