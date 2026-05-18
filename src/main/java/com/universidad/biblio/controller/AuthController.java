package com.universidad.biblio.controller;

import com.universidad.biblio.model.User;
import com.universidad.biblio.service.BookService;
import com.universidad.biblio.service.LoanService;
import com.universidad.biblio.service.UserServi;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserServi service;
    private final BookService bookService;
    private final LoanService loanService;

    public AuthController(UserServi service, BookService bookService, LoanService loanService) {
        this.service = service;
        this.bookService = bookService;
        this.loanService = loanService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/login.html")
    public String loginHtml() {
        return "redirect:/login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {

        model.addAttribute("user", new User());

        return "auth/registro";
    }

    @PostMapping("/registro")
    public String guardar(@ModelAttribute User user) {

        service.register(user);

        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        loanService.markExpiredLoans();
        model.addAttribute("totalBooks", bookService.count());
        model.addAttribute("activeLoans", loanService.countActive());
        model.addAttribute("totalUsers", service.count());
        model.addAttribute("expiredLoans", loanService.countExpired());
        return "dashboard";
    }
}
