package com.example.projetsantesecurise.controller;

import com.example.projetsantesecurise.models.Groupe;
import com.example.projetsantesecurise.models.PW;
import com.example.projetsantesecurise.models.Professor;
import com.example.projetsantesecurise.models.Student;
import com.example.projetsantesecurise.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/prof")
public class GroupeController {

    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private PWRepository pwRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private StudentRepository studentRepository;


    @GetMapping("/groupe")
    public String showIndex(Model model, Principal principal) {

        String email = principal.getName();
        Professor professor = professorRepository.findByEmail(email);
        model.addAttribute("professor",professor);
        model.addAttribute("newGroupe",new Groupe());
        //model.addAttribute("groupes",groupeRepository.findAll());

        return "groupeCrud";
    }

    @PostMapping("/groupe/add")
    public String addGroupe(@ModelAttribute Groupe newGroupe, @RequestParam Long professorId) {
        Professor professor = professorRepository.findById(professorId).orElseThrow();
        newGroupe.setProfessor(professor);
        groupeRepository.save(newGroupe);

        return "redirect:/prof/groupe";
    }
    @GetMapping("/groupe/findOne/{id}")
    @ResponseBody
    public Groupe findOne(@PathVariable("id") Long id) {
        Groupe groupe = groupeRepository.findById(id).orElse(null);

        return groupe;
    }
//////////////////////////////////////////////////////
private String encodePhoto(byte[] photo) {
    return (photo != null && photo.length > 0) ? Base64.getEncoder().encodeToString(photo) : "";
}

    @GetMapping("/groupe/students/{id}")
    public String showStudents(@PathVariable("id") long id,Model model,Principal principal) {
        String email = principal.getName();
        Professor professor = professorRepository.findByEmail(email);
        List<String> encodedPhotos = new ArrayList<>();
        model.addAttribute("professor",professor);
        System.out.println("professor  :"+ professor);
        Groupe groupe = groupeRepository.findById(id).orElse(null);
        model.addAttribute("groupe",groupe);

        for (Student student : groupe.getStudents()) {
            encodedPhotos.add(encodePhoto(student.getPhoto()));
        }

        model.addAttribute("encodedPhotos", encodedPhotos);
        model.addAttribute("newStudent", new Student());
        System.out.println("groupe  :"+ groupe);
        return "studentCrud";
    }

///////////////////////////////////////////////////////

// ////////////////////////////////////////////////////

    @GetMapping("/groupe/pws/{id}")
    public String showPws(@PathVariable("id") long id, Model model, Principal principal) {
        String email = principal.getName();
        Professor professor = professorRepository.findByEmail(email);
        model.addAttribute("professor", professor);
        System.out.println("professor : " + professor);

        Groupe groupe = groupeRepository.findById(id).orElse(null);

        // Assuming you have a method in your repository to retrieve PWs by Groupe
        Set<PW> pws = pwRepository.findByGroupes(groupe);

        model.addAttribute("pws", pws);

        return "PWList";
    }

///////////////////////////////////////////////////////

    @PostMapping("/groupe/update")
    public String updateGroupe(@ModelAttribute Groupe newGroupe, @RequestParam Long professorId) {
        Professor professor = professorRepository.findById(professorId).orElseThrow();
        newGroupe.setProfessor(professor);
        groupeRepository.save(newGroupe);
        return "redirect:/prof/groupe";
    }


    @GetMapping("/groupe/delete/{id}")
    public String deleteGroupe(@PathVariable("id") long id) {
        Groupe groupe = groupeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid groupe Id:" + id));
        groupeRepository.delete(groupe);
        return "redirect:/prof/groupe";
    }

}
