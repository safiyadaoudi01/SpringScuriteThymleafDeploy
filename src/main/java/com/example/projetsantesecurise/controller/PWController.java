package com.example.projetsantesecurise.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.example.projetsantesecurise.models.*;
import com.example.projetsantesecurise.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/prof")
@Controller
public class PWController {

    @Autowired
    private PWRepository pwRepository;


    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private ToothRepository toothRepository;
    @Autowired
    private ProfessorRepository profrepo;

    @Autowired
    private UtilisateurRepository utilisateurRepository;



    @GetMapping("/pw")
    public String showPWList(Model model, Principal principal) {
        // Récupérez la liste des PWs depuis la base de données
        String email = principal.getName();
        Professor professor = profrepo.findByEmail(email);
        model.addAttribute("professor",professor);

        List<PW> pwList = (List<PW>) pwRepository.findAll();

        // Ajoutez la liste des PWs au modèle
        model.addAttribute("pwList", pwList);
        model.addAttribute("groupes", groupeRepository.findAll());
        model.addAttribute("teeth", toothRepository.findAll()); // Assurez-vous d'avoir un repository pour Tooth
        model.addAttribute("pw", new PW());
        model.addAttribute("mode", "add");

        return "PWCrud";
    }
    @GetMapping("/pw/findOne/{id}")
    @ResponseBody
    public PW findOne(@PathVariable("id") Long id) {
        PW pw = pwRepository.findById(id).orElse(null);

        return pw;
    }



    @PostMapping("/pw/addpw")
    public String addPW(@Validated PW pw, BindingResult result, Model model,
                        @RequestParam("selectedGroupes") List<Long> selectedGroupesIds, @RequestParam("docsFile") MultipartFile docsFile) throws IOException {
        if (result.hasErrors()) {
            return "Pw";
        }
        PW savedPW = pwRepository.save(pw);
        List<Groupe> selectedGroupes = (List<Groupe>) groupeRepository.findAllById(selectedGroupesIds);

        if (selectedGroupes != null && !selectedGroupes.isEmpty()) {
            savedPW.getGroupes().addAll(selectedGroupes);
        }
        if (docsFile != null && !docsFile.isEmpty()) {
            byte[] docsBytes = docsFile.getBytes();
            pw.setDocs(docsBytes);


        }
        savedPW.setObjectif(pw.getObjectif());
        savedPW.setDocs(pw.getDocs());

        pwRepository.save(savedPW);

        System.out.println("Selected Group IDs: " + selectedGroupesIds);

        return "redirect:/prof/pw";
    }

    @GetMapping("/pw/delete/{id}")
    public String deletePW(@PathVariable Long id) {
        pwRepository.deleteById(id);
        return "redirect:/prof/pw";
    }


    @PostMapping("/pw/update")
    public String updateGroupe(@ModelAttribute PW pw, @RequestParam Long toothId,@RequestParam("selectedGroupes") List<Long> selectedGroupesIds, @RequestParam("editDocsFile") MultipartFile editDocsFile) {
        Tooth tooth=toothRepository.findById(toothId).orElseThrow();
        pw.setTooth(tooth);
        List<Groupe> selectedGroupes = (List<Groupe>) groupeRepository.findAllById(selectedGroupesIds);

        if (selectedGroupes != null && !selectedGroupes.isEmpty()) {
            pw.getGroupes().addAll(selectedGroupes);
        }
        if (editDocsFile != null && !editDocsFile.isEmpty()) {
            try {
                pw.setDocs(editDocsFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            PW existingPw = pwRepository.findById(pw.getId()).orElse(null);
            if (existingPw != null && existingPw.getDocs() != null) {
                pw.setDocs(existingPw.getDocs());
            }
        }

        System.out.println(selectedGroupesIds);
        pwRepository.save(pw);

        return "redirect:/prof/pw";
    }

    @GetMapping(value = "/pw/download/{id}", produces = "application/octet-stream")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long id) {
        Optional<PW> optionalPW = pwRepository.findById(id);

        if (optionalPW.isPresent()) {
            PW pw = optionalPW.get();
            byte[] fileContent = pw.getDocs();
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + pw.getTitle() + ".doc")
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileContent.length)
                    .body(resource);
        } else {
            // Handle the case where PW is not found
            return ResponseEntity.notFound().build();
        }
    }



    }
