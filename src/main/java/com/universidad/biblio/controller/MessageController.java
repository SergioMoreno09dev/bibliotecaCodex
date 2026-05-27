package com.universidad.biblio.controller;

import com.universidad.biblio.model.Message;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.UserRepository;
import com.universidad.biblio.service.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/messages", "/api/mensajes", "/api/mensjaes"})
public class MessageController {

    private final MessageService service;
    private final UserRepository userRepository;

    public MessageController(MessageService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Message> list(@RequestParam(required = false) Boolean sent,
                              @RequestParam(required = false) Integer userId) {

        boolean admin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        int currentUserId = currentUserId();

        if (userId != null && admin) {
            return Boolean.TRUE.equals(sent) ? service.bySender(userId) : service.byReceiver(userId);
        }

        return Boolean.TRUE.equals(sent) ? service.bySender(currentUserId) : service.byReceiver(currentUserId);
    }

    @GetMapping("/bandeja-entrada")
    public List<Message> inbox() {
        return service.byReceiver(currentUserId());
    }

    @PostMapping
    public Message send(@Valid @RequestBody MessageRequest request) {
        int senderId = currentUserId();
        return service.send(senderId, request.receiverId(), request.subject(), request.content());
    }

    @PatchMapping("/{id}/read")
    public Message markAsRead(@PathVariable Long id) {
        int receiverId = currentUserId();
        return service.markAsRead(id, receiverId);
    }

    @GetMapping("/unread-count")
    public long unreadCount() {
        return service.unreadCount(currentUserId());
    }

    private int currentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }

    public record MessageRequest(@NotNull Integer receiverId,
                                 @NotBlank String subject,
                                 @NotBlank String content) {
    }
}
