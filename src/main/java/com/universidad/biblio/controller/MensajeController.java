package com.universidad.biblio.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.universidad.biblio.model.Mensaje;
import com.universidad.biblio.service.MensajeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {
    private final MensajeService service;

    public MensajeController(MensajeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Mensaje> enviar(@Valid @RequestBody MensajeRequest request,
                                          Authentication authentication) {
        Mensaje mensaje = service.enviar(authentication.getName(),
                request.destinatario(), request.asunto(), request.contenido());
        return ResponseEntity.created(URI.create("/api/mensajes/" + mensaje.getId())).body(mensaje);
    }

    @GetMapping("/bandeja-entrada")
    public List<Mensaje> bandejaEntrada(Authentication authentication) {
        return service.bandejaEntrada(authentication.getName());
    }

    @GetMapping("/enviados")
    public List<Mensaje> enviados(Authentication authentication) {
        return service.enviados(authentication.getName());
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<Mensaje> marcarComoLeido(@PathVariable Long id, Authentication authentication) {
        return service.marcarComoLeido(id, authentication.getName())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/no-leidos/count")
    public Map<String, Long> contarNoLeidos(Authentication authentication) {
        return Map.of("count", service.contarNoLeidos(authentication.getName()));
    }

    public record MensajeRequest(
            @JsonAlias({"destinatarioUsername", "nombreUsuarioDestinatario", "username", "receptor"})
            @NotBlank String destinatario,
            @NotBlank String asunto,
            @NotBlank String contenido) {
    }
}
