package com.universidad.biblio.service;

import com.universidad.biblio.model.EstadoSolicitud;
import com.universidad.biblio.model.Solicitud;
import com.universidad.biblio.model.TipoSolicitud;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.SolicitudRepository;
import com.universidad.biblio.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudService {
    private final SolicitudRepository solicitudRepository;
    private final UserRepository userRepository;

    public SolicitudService(SolicitudRepository solicitudRepository, UserRepository userRepository) {
        this.solicitudRepository = solicitudRepository;
        this.userRepository = userRepository;
    }

    public Solicitud crear(String emailSolicitante, TipoSolicitud tipo, String descripcion) {
        User solicitante = userRepository.findByEmailIgnoreCase(emailSolicitante)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        return solicitudRepository.save(new Solicitud(solicitante, tipo, descripcion.trim()));
    }

    public List<Solicitud> misSolicitudes(String emailSolicitante) {
        return solicitudRepository.findBySolicitanteEmailIgnoreCase(emailSolicitante);
    }

    public List<Solicitud> listarTodas() {
        return solicitudRepository.findAll();
    }

    public Optional<Solicitud> aprobar(Long id, String observacion) {
        return resolver(id, EstadoSolicitud.APROBADA, observacion);
    }

    public Optional<Solicitud> rechazar(Long id, String observacion) {
        return resolver(id, EstadoSolicitud.RECHAZADA, observacion);
    }

    private Optional<Solicitud> resolver(Long id, EstadoSolicitud estado, String observacion) {
        if (observacion == null || observacion.trim().isEmpty()) {
            throw new RuntimeException("La observacion es obligatoria");
        }
        return solicitudRepository.findById(id)
                .map(solicitud -> {
                    if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
                        throw new RuntimeException("Solo se pueden resolver solicitudes pendientes");
                    }
                    solicitud.setEstado(estado);
                    solicitud.setObservacion(observacion.trim());
                    solicitud.setFechaResolucion(LocalDateTime.now());
                    return solicitudRepository.save(solicitud);
                });
    }
}
