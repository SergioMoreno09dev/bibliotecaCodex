package com.universidad.biblio.controller;

import com.universidad.biblio.model.Solicitud;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.UserRepository;
import com.universidad.biblio.service.SolicitudService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService service;
    private final UserRepository userRepository;

    public SolicitudController(SolicitudService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Solicitud create(@RequestBody CreateRequest request) {
        int requesterId = currentUserId();
        return service.create(requesterId, request.type(), request.description());
    }

    @GetMapping
    public List<Solicitud> list(@RequestParam(required = false) Integer userId) {
        boolean admin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (admin) {
            if (userId != null) {
                return service.byRequester(userId);
            }
            return service.listAll();
        }

        int current = currentUserId();
        return service.byRequester(current);
    }

    @PatchMapping("/{id}/approve")
    public Solicitud approve(@PathVariable Long id, @RequestBody ResolveRequest request) {
        requireAdmin();
        return service.approve(id, request.observation());
    }

    @PatchMapping("/{id}/reject")
    public Solicitud reject(@PathVariable Long id, @RequestBody ResolveRequest request) {
        requireAdmin();
        return service.reject(id, request.observation());
    }

    private int currentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }

    private void requireAdmin() {
        boolean admin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!admin) throw new RuntimeException("Acceso denegado");
    }

    public record CreateRequest(String type, String description) {
    }

    public record ResolveRequest(String observation) {
    }
}
