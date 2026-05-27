package com.universidad.biblio.controller;

import com.universidad.biblio.model.Solicitud;
import com.universidad.biblio.model.TipoSolicitud;
import com.universidad.biblio.service.SolicitudService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {
    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Solicitud> crear(@Valid @RequestBody SolicitudRequest request,
                                           Authentication authentication) {
        Solicitud solicitud = service.crear(authentication.getName(), request.tipo(), request.descripcion());
        return ResponseEntity.created(URI.create("/api/solicitudes/" + solicitud.getId())).body(solicitud);
    }

    @GetMapping("/mis-solicitudes")
    public List<Solicitud> misSolicitudes(Authentication authentication) {
        return service.misSolicitudes(authentication.getName());
    }

    @GetMapping
    public List<Solicitud> listarTodas() {
        return service.listarTodas();
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<Solicitud> aprobar(@PathVariable Long id, @RequestParam String observacion) {
        return service.aprobar(id, observacion)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<Solicitud> rechazar(@PathVariable Long id, @RequestParam String observacion) {
        return service.rechazar(id, observacion)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record SolicitudRequest(@NotNull TipoSolicitud tipo, @NotBlank String descripcion) {
    }
}
