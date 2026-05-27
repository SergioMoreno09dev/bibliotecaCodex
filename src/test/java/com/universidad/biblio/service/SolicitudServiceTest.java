package com.universidad.biblio.service;

import com.universidad.biblio.model.EstadoSolicitud;
import com.universidad.biblio.model.Solicitud;
import com.universidad.biblio.model.TipoSolicitud;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.SolicitudRepository;
import com.universidad.biblio.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {
    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SolicitudService service;

    @Test
    void crearAsignaSolicitanteEstadoYFecha() {
        User user = new User(1, "Ana", "ana@test.com", "secret", "LECTOR");
        when(userRepository.findByEmailIgnoreCase("ana@test.com")).thenReturn(Optional.of(user));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Solicitud solicitud = service.crear("ana@test.com", TipoSolicitud.SOPORTE, "  Necesito ayuda  ");

        assertThat(solicitud.getSolicitante()).isEqualTo(user);
        assertThat(solicitud.getTipo()).isEqualTo(TipoSolicitud.SOPORTE);
        assertThat(solicitud.getDescripcion()).isEqualTo("Necesito ayuda");
        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE);
        assertThat(solicitud.getFechaCreacion()).isNotNull();
        assertThat(solicitud.getFechaResolucion()).isNull();
    }

    @Test
    void misSolicitudesConsultaPorEmailDelSolicitante() {
        List<Solicitud> esperadas = List.of(new Solicitud());
        when(solicitudRepository.findBySolicitanteEmailIgnoreCase("lector@test.com")).thenReturn(esperadas);

        List<Solicitud> solicitudes = service.misSolicitudes("lector@test.com");

        assertThat(solicitudes).isSameAs(esperadas);
        verify(solicitudRepository).findBySolicitanteEmailIgnoreCase("lector@test.com");
    }

    @Test
    void aprobarRegistraEstadoObservacionYFechaResolucion() {
        Solicitud solicitud = new Solicitud(new User(), TipoSolicitud.ACCESO, "Acceso requerido");
        when(solicitudRepository.findById(5L)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Solicitud> aprobada = service.aprobar(5L, "  Aprobado por admin  ");

        assertThat(aprobada).isPresent();
        assertThat(aprobada.get().getEstado()).isEqualTo(EstadoSolicitud.APROBADA);
        assertThat(aprobada.get().getObservacion()).isEqualTo("Aprobado por admin");
        assertThat(aprobada.get().getFechaResolucion()).isNotNull();
    }

    @Test
    void rechazarRetornaVacioSiNoExiste() {
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Solicitud> rechazada = service.rechazar(99L, "No aplica");

        assertThat(rechazada).isEmpty();
    }

    @Test
    void resolverRequiereObservacionConTexto() {
        assertThatThrownBy(() -> service.aprobar(1L, " "))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("La observacion es obligatoria");
    }

    @Test
    void resolverRechazaSolicitudesQueNoEstanPendientes() {
        Solicitud solicitud = new Solicitud(new User(), TipoSolicitud.ACCESO, "Acceso requerido");
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        when(solicitudRepository.findById(7L)).thenReturn(Optional.of(solicitud));

        assertThatThrownBy(() -> service.rechazar(7L, "No aplica"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Solo se pueden resolver solicitudes pendientes");
    }

    @Test
    void listarTodasUsaRepositorio() {
        when(solicitudRepository.findAll()).thenReturn(List.of(new Solicitud(), new Solicitud()));

        List<Solicitud> solicitudes = service.listarTodas();

        assertThat(solicitudes).hasSize(2);
        verify(solicitudRepository).findAll();
    }
}
