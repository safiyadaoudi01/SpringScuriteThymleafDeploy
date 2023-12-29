package com.example.projetsantesecurise.controller;

import com.example.projetsantesecurise.models.*;

import com.example.projetsantesecurise.repository.ProfessorRepository;
import com.example.projetsantesecurise.repository.StudentRepository;
import com.example.projetsantesecurise.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Random;

@Controller
@SessionAttributes({"loggedInUser","verificationCode"})
public class UserController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private static final String FROM_EMAIL = "hajarmachmoum546@gmail.com";  // Remplacez par votre adresse e-mail
    private static final String SUBJECT = "Récupération de mot de passe";
    @GetMapping("/userlogout")
    public String logout() {
        return "logout";
    }

    @GetMapping("/users")
    public ResponseEntity<List<Student>> getAllUsers() {
        List<Student> users = (List<Student>) studentRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("utilisateur", new Professor());
        return "signup";
    }
    @PostMapping("/signup")
    public String signup(@ModelAttribute Professor utilisateur, BindingResult result, Model model) {
        System.out.println("Received signup request: " + utilisateur.toString());
        try{


        Professor professor = new Professor();
        professor.setUsername(utilisateur.getUsername());
        String encodedPassword = BCrypt.hashpw(utilisateur.getPassword(),BCrypt.gensalt(12));
        professor.setPassword(encodedPassword);
        professor.setEmail(utilisateur.getEmail());
        professor.setGrade(utilisateur.getGrade());



        professor.setRole("ROLE_PROF");

        professorRepository.save(professor);

        } catch (Exception e) {
            System.err.println("Error while saving user: " + e.getMessage());
            e.printStackTrace();
            // Gérez l'erreur selon vos besoins
            return "signup"; // Revenir au formulaire en cas d'erreur
        }

        model.addAttribute("utilisateur", utilisateur);
        return "redirect:/login2";
    }



    @GetMapping("/signin")
    public String showLoginForm() {

        return "login2";
    }

    @GetMapping("/admin")
    public String showadminForm() {

        return "redirect:/professors";
    }
    @ModelAttribute
    public void commonUser(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            User user = utilisateurRepository.findByEmail(email);
            m.addAttribute("user", user);
        }

    }
    @PostMapping("/login2")
    public String login(@ModelAttribute("user") User utilisateur, BindingResult bindingResult, Model model) {
        User existingUser = utilisateurRepository.findByEmail(utilisateur.getEmail());

        if (existingUser != null && BCrypt.checkpw(utilisateur.getPassword(), existingUser.getPassword())) {
            // L'utilisateur est connecté avec succès

            if ("ROLE_PATIENT".equals(existingUser.getRole())) {
                model.addAttribute("loggedInUser", existingUser);
                System.out.println(existingUser);
                // Rediriger vers la page patient
                return "redirect:/patient";
            } else if ("ROLE_MEDECIN".equals(existingUser.getRole())) {
                model.addAttribute("loggedInUser", existingUser);
                System.out.println(existingUser);

                // Rediriger vers la page médecin
                return "redirect:/medecin";
            } else {
                // Gérez d'autres rôles si nécessaire
            }
        } else {
            // Les informations de connexion sont incorrectes, renvoyer vers la page de connexion avec un message d'erreur
            model.addAttribute("error", "Invalid username or password");
            return "login2";
        }
        return null;
    }






    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("user", new User());
        return "forgot-password";
    }
    @Transactional
    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam("email") String email, Model model,HttpSession session) {
        User utilisateur = utilisateurRepository.findByEmail(email);

        if (utilisateur != null) {
            String verificationCode = generateVerificationCode();


            // Envoyer le code de vérification par e-mail
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Réinitialisation du mot de passe");
            message.setText("Votre code de vérification est : " + verificationCode);
            javaMailSender.send(message);
            model.addAttribute("verificationCode", verificationCode);
            session.setAttribute("email", email);


            // Ajoutez la redirection vers la page reset-password ici
            return "redirect:/reset-password";
        } else {
            model.addAttribute("error", "Aucun utilisateur trouvé avec cet e-mail.");
        }

        return "redirect:/forgot-password";
    }


    private String generateVerificationCode() {
        // Génération d'un code aléatoire de 6 chiffres (à personnaliser selon vos besoins)
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    // Ajoutez cette méthode à votre UtilisateurController
    @GetMapping("/reset-password")
    public String showResetPasswordForm(Model model) {
        model.addAttribute("user", new User());
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@ModelAttribute("user") User utilisateur,
                                @RequestParam("verificationCode") String userEnteredCode,
                                HttpSession session,

                                BindingResult bindingResult, Model model) {
        // Récupérer le code de vérification de la session
        String verificationCode = (String) model.getAttribute("verificationCode");
        String email = (String) session.getAttribute("email");

        System.out.println("Email attribute in resetPassword: " + email);

        System.out.println(verificationCode);
        System.out.println(userEnteredCode);


        if (verificationCode != null && verificationCode.equals(userEnteredCode)) {

            session.setAttribute("email", email);

            return "redirect:/newpassword";
        } else {
            // Le code de vérification est incorrect, affichez un message d'erreur
            model.addAttribute("error", "Code de vérification incorrect.");
            return "reset-password";
        }
    }
    @GetMapping("/newpassword")
    public String showNewPasswordForm(Model model, HttpSession session) {
        // Récupérez l'e-mail de la session
        String email = (String) session.getAttribute("email");

        // Vérifiez si l'e-mail est présent dans la session
        if (email != null) {
            System.out.println("Email attribute found in the session: " + email);
        } else {
            System.out.println("Email attribute not found in the session");
            // Gérez le cas où l'e-mail n'est pas trouvé dans la session
        }

        // Passez l'e-mail à la page
        model.addAttribute("email", email);
        return "newpassword";
    }



    @PostMapping("/newpassword")
    public String updatePassword(
            @RequestParam("newPassword") String newPassword,
            Model model,HttpSession session) {
        // Récupérer l'utilisateur par e-mail
        String email = (String) session.getAttribute("email");
        System.out.println(email);

        User loggedInUserEmail = utilisateurRepository.findByEmail(email);
        System.out.println(loggedInUserEmail);

        if (loggedInUserEmail != null) {
            // Mettre à jour le mot de passe de l'utilisateur avec le nouveau mot de passe
            loggedInUserEmail.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
            utilisateurRepository.save(loggedInUserEmail);

            // Rediriger vers une page de succès ou une autre page appropriée
            return "redirect:/success";
        } else {
            // L'utilisateur n'a pas été trouvé, gérer l'erreur
            model.addAttribute("error", "Utilisateur non trouvé");
            return "newpassword";
        }

    }














}
