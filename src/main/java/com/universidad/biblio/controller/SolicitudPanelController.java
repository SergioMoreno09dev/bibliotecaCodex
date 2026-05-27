package com.universidad.biblio.controller;

import com.universidad.biblio.model.EstadoSolicitud;
import com.universidad.biblio.model.Solicitud;
import com.universidad.biblio.repository.SolicitudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SolicitudPanelController {
    private final SolicitudRepository solicitudRepository;

    public SolicitudPanelController(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    @GetMapping("/admin/solicitudes/panel")
    public String panel(Model model) {
        List<Solicitud> solicitudes = solicitudRepository.findAll();

        long pendientes = contarPorEstado(solicitudes, EstadoSolicitud.PENDIENTE);
        long aprobadas = contarPorEstado(solicitudes, EstadoSolicitud.APROBADA);
        long rechazadas = contarPorEstado(solicitudes, EstadoSolicitud.RECHAZADA);

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("totalSolicitudes", solicitudes.size());
        model.addAttribute("totalPendientes", pendientes);
        model.addAttribute("totalAprobadas", aprobadas);
        model.addAttribute("totalRechazadas", rechazadas);

        return "admin/solicitudes-panel";
    }

    private long contarPorEstado(List<Solicitud> solicitudes, EstadoSolicitud estado) {
        return solicitudes.stream()
                .filter(solicitud -> solicitud.getEstado() == estado)
                .count();
    }
}
