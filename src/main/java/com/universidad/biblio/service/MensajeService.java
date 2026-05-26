package com.universidad.biblio.service;

import com.universidad.biblio.model.Mensaje;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.MensajeRepository;
import com.universidad.biblio.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MensajeService {
    private final MensajeRepository mensajeRepository;
    private final UserRepository userRepository;

    public MensajeService(MensajeRepository mensajeRepository, UserRepository userRepository) {
        this.mensajeRepository = mensajeRepository;
        this.userRepository = userRepository;
    }

    public Mensaje enviar(String emisorEmail, String destinatario, String asunto, String contenido) {
        User emisor = currentUser(emisorEmail);
        User receptor = findDestinatario(destinatario);
        Mensaje mensaje = new Mensaje(emisor, receptor, asunto.trim(), contenido.trim(), false, new Date());
        return mensajeRepository.save(mensaje);
    }

    public List<Mensaje> bandejaEntrada(String userEmail) {
        User user = currentUser(userEmail);
        return mensajeRepository.findByReceptorIdOrderByFechaEnvioDesc(user.getId());
    }

    public List<Mensaje> enviados(String userEmail) {
        User user = currentUser(userEmail);
        return mensajeRepository.findByEmisorIdOrderByFechaEnvioDesc(user.getId());
    }

    public Optional<Mensaje> marcarComoLeido(Long id, String userEmail) {
        User user = currentUser(userEmail);
        return mensajeRepository.findByIdAndReceptorId(id, user.getId())
                .map(mensaje -> {
                    mensaje.setLeido(true);
                    return mensajeRepository.save(mensaje);
                });
    }

    public long contarNoLeidos(String userEmail) {
        User user = currentUser(userEmail);
        return mensajeRepository.countByReceptorIdAndLeidoFalse(user.getId());
    }

    private User currentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }

    private User findDestinatario(String destinatario) {
        if (destinatario == null || destinatario.isBlank()) {
            throw new RuntimeException("Destinatario requerido");
        }

        String value = destinatario.trim();
        return userRepository.findByEmailIgnoreCase(value)
                .or(() -> userRepository.findFirstByNameIgnoreCase(value))
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));
    }
}
