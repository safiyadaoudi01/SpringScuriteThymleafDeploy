package com.example.projetsantesecurise.controller;

import com.example.projetsantesecurise.models.Groupe;
import com.example.projetsantesecurise.models.Professor;
import com.example.projetsantesecurise.models.Student;
import com.example.projetsantesecurise.models.Tooth;
import com.example.projetsantesecurise.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/prof")
public class ToothController {
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private ToothRepository toothRepository;
    @Autowired
    private ProfessorRepository professorRepository;



    @GetMapping("/tooth")
    public String showIndex(Model model, Principal principal) {

        String email = principal.getName();
        Professor professor = professorRepository.findByEmail(email);
        model.addAttribute("professor",professor);
        model.addAttribute("newTooth",new Tooth());
        model.addAttribute("tooths",toothRepository.findAll());

        return "toothCrud";
    }

    @PostMapping("/tooth/add")
    public String addTooth(@ModelAttribute Tooth newTooth) {

        toothRepository.save(newTooth);

        return "redirect:/prof/tooth";
    }
    @GetMapping("/tooth/findOne/{id}")
    @ResponseBody
    public Tooth findOne(@PathVariable("id") Long id) {
        Tooth tooth = toothRepository.findById(id).orElse(null);

        return tooth;
    }

    @PostMapping("/tooth/update")
    public String updateTooth(@ModelAttribute Tooth newTooth) {

        toothRepository.save(newTooth);
        return "redirect:/prof/tooth";
    }


    @GetMapping("/tooth/delete/{id}")
    public String deleteTooth(@PathVariable("id") long id) {
        Tooth tooth = toothRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tooth Id:" + id));
        toothRepository.delete(tooth);
        return "redirect:/prof/tooth";
    }

}
