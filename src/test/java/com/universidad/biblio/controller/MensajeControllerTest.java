package com.universidad.biblio.controller;
import com.universidad.biblio.model.Mensaje;
import com.universidad.biblio.model.User;
import com.universidad.biblio.service.MensajeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MensajeControllerTest {
    private MensajeService service;
    private MockMvc mockMvc;
    private TestingAuthenticationToken authentication;

    @BeforeEach
    void setUp() {
        service = mock(MensajeService.class);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new MensajeController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .setValidator(validator)
                .build();

        authentication = new TestingAuthenticationToken("ana@test.com", "secret");
    }

    @Test
    void enviarMensajeRetorna201YPersisteConUsuarioAutenticado() throws Exception {
        Mensaje mensaje = mensaje(15L, "ana@test.com", "carlos@test.com", "Consulta", "Hola");
        when(service.enviar("ana@test.com", "Carlos", "Consulta", "Hola")).thenReturn(mensaje);

        mockMvc.perform(post("/api/mensajes")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "destinatario": "Carlos",
                                  "asunto": "Consulta",
                                  "contenido": "Hola"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/mensajes/15"))
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.asunto").value("Consulta"))
                .andExpect(jsonPath("$.contenido").value("Hola"))
                .andExpect(jsonPath("$.leido").value(false))
                .andExpect(jsonPath("$.emisor.email").value("ana@test.com"))
                .andExpect(jsonPath("$.receptor.email").value("carlos@test.com"));

        verify(service).enviar("ana@test.com", "Carlos", "Consulta", "Hola");
    }

    @Test
    void enviarMensajeConDatosInvalidosRetorna400() throws Exception {
        mockMvc.perform(post("/api/mensajes")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "destinatario": "",
                                  "asunto": "Consulta",
                                  "contenido": "Hola"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bandejaEntradaListaMensajesRecibidosDelUsuarioActual() throws Exception {
        when(service.bandejaEntrada("ana@test.com")).thenReturn(List.of(
                mensaje(1L, "carlos@test.com", "ana@test.com", "Uno", "Contenido uno"),
                mensaje(2L, "laura@test.com", "ana@test.com", "Dos", "Contenido dos")
        ));

        mockMvc.perform(get("/api/mensajes/bandeja-entrada")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].receptor.email").value("ana@test.com"))
                .andExpect(jsonPath("$[1].receptor.email").value("ana@test.com"));

        verify(service).bandejaEntrada("ana@test.com");
    }

    @Test
    void enviadosListaMensajesEnviadosPorUsuarioActual() throws Exception {
        when(service.enviados("ana@test.com")).thenReturn(List.of(
                mensaje(3L, "ana@test.com", "carlos@test.com", "Enviado", "Contenido")
        ));

        mockMvc.perform(get("/api/mensajes/enviados")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].emisor.email").value("ana@test.com"));

        verify(service).enviados("ana@test.com");
    }

    @Test
    void marcarComoLeidoRetorna200CuandoElMensajeExisteParaElUsuario() throws Exception {
        Mensaje mensaje = mensaje(8L, "carlos@test.com", "ana@test.com", "Leer", "Pendiente");
        mensaje.setLeido(true);
        when(service.marcarComoLeido(8L, "ana@test.com")).thenReturn(Optional.of(mensaje));

        mockMvc.perform(put("/api/mensajes/8/leer")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.leido").value(true));

        verify(service).marcarComoLeido(8L, "ana@test.com");
    }

    @Test
    void marcarComoLeidoRetorna404CuandoNoExisteONoPerteneceAlUsuario() throws Exception {
        when(service.marcarComoLeido(99L, "ana@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/mensajes/99/leer")
                        .principal(authentication))
                .andExpect(status().isNotFound());

        verify(service).marcarComoLeido(99L, "ana@test.com");
    }

    @Test
    void contarNoLeidosRetornaJsonConCount() throws Exception {
        when(service.contarNoLeidos("ana@test.com")).thenReturn(4L);

        mockMvc.perform(get("/api/mensajes/no-leidos/count")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(4));

        verify(service).contarNoLeidos("ana@test.com");
    }

    private Mensaje mensaje(Long id, String emisorEmail, String receptorEmail, String asunto, String contenido) {
        User emisor = new User(1, "Emisor", emisorEmail, "secret", "LECTOR");
        User receptor = new User(2, "Receptor", receptorEmail, "secret", "LECTOR");
        Mensaje mensaje = new Mensaje(emisor, receptor, asunto, contenido, false, new Date());
        mensaje.setId(id);
        return mensaje;
    }
}
