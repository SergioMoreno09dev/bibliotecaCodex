package com.universidad.biblio.config;

import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.UserRepository;
import com.universidad.biblio.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.Set;

@Component
public class AuditInterceptor implements HandlerInterceptor {
    private static final Set<String> AUDITED_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public AuditInterceptor(AuditLogService auditLogService, UserRepository userRepository) {
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (!AUDITED_METHODS.contains(request.getMethod()) || response.getStatus() >= 400) {
            return;
        }

        try {
            Integer userId = currentUserId();
            String action = request.getMethod() + " " + request.getRequestURI();
            String description = "Accion ejecutada con estado HTTP " + response.getStatus();
            auditLogService.add(action, description, userId);
        } catch (RuntimeException ignored) {
            // La auditoria no debe impedir que termine la accion principal.
        }
    }

    private Integer currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        Optional<User> user = userRepository.findByEmail(authentication.getName());
        return user.map(User::getId).orElse(null);
    }
}
