package com.universidad.biblio.service;

import com.universidad.biblio.model.Solicitud;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.SolicitudRepository;
import com.universidad.biblio.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SolicitudService {

    private static final Set<String> ALLOWED_TYPES = Set.of("soporte", "acceso", "información", "informacion");
    private static final String STATE_PENDING = "pendiente";
    private static final String STATE_APPROVED = "aprobada";
    private static final String STATE_REJECTED = "rechazada";

    private final SolicitudRepository solicitudRepository;
    private final UserRepository userRepository;

    public SolicitudService(SolicitudRepository solicitudRepository, UserRepository userRepository) {
        this.solicitudRepository = solicitudRepository;
        this.userRepository = userRepository;
    }

    public Solicitud create(int requesterId, String type, String description) {
        String lowerType = type == null ? "" : type.trim().toLowerCase();
        if (!ALLOWED_TYPES.contains(lowerType)) {
            throw new RuntimeException("Tipo de solicitud no permitido");
        }

        User requester = userRepository.findById(requesterId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Solicitud s = new Solicitud();
        s.setRequester(requester);
        s.setType(lowerType);
        s.setDescription(description);
        s.setState(STATE_PENDING);
        s.setCreatedAt(new Date());
        return solicitudRepository.save(s);
    }

    public List<Solicitud> byRequester(int requesterId) {
        return solicitudRepository.findByRequesterIdOrderByCreatedAtDesc(requesterId);
    }

    public List<Solicitud> listAll() {
        return solicitudRepository.findAll();
    }

    public Solicitud approve(Long id, String observation) {
        Solicitud s = solicitudRepository.findById(id).orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        s.setState(STATE_APPROVED);
        s.setObservation(observation);
        s.setResolvedAt(new Date());
        return solicitudRepository.save(s);
    }

    public Solicitud reject(Long id, String observation) {
        Solicitud s = solicitudRepository.findById(id).orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        s.setState(STATE_REJECTED);
        s.setObservation(observation);
        s.setResolvedAt(new Date());
        return solicitudRepository.save(s);
    }
}
