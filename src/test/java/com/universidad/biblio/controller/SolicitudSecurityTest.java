package com.universidad.biblio.controller;

import com.universidad.biblio.model.Solicitud;
import com.universidad.biblio.model.TipoSolicitud;
import com.universidad.biblio.model.User;
import com.universidad.biblio.service.SolicitudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SolicitudSecurityTest {
    private final WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private SolicitudService solicitudService;

    SolicitudSecurityTest(WebApplicationContext context) {
        this.context = context;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void crearSolicitudSinAutenticacionRetorna401O403() throws Exception {
        mockMvc.perform(post("/api/solicitudes")
                        .with(user("lector@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tipo": "SOPORTE",
                                  "descripcion": "Necesito ayuda"
                                }
                                """))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "lector@test.com")
    void crearSolicitudConUsuarioAutenticadoRetorna201() throws Exception {
        Solicitud solicitud = new Solicitud(
                new User(1, "Lector", "lector@test.com", "secret", "LECTOR"),
                TipoSolicitud.SOPORTE,
                "Necesito ayuda"
        );
        solicitud.setId(10L);
        when(solicitudService.crear("lector@test.com", TipoSolicitud.SOPORTE, "Necesito ayuda"))
                .thenReturn(solicitud);

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tipo": "SOPORTE",
                                  "descripcion": "Necesito ayuda"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "lector@test.com", roles = "USER")
    void aprobarSolicitudConUsuarioSinRolAdminRetorna403SinEjecutarServicio() throws Exception {
        mockMvc.perform(put("/api/solicitudes/999/aprobar")
                        .with(user("lector@test.com").roles("USER"))
                        .param("observacion", "Aprobada"))
                .andExpect(status().isForbidden());

        verify(solicitudService, never()).aprobar(eq(999L), any());
    }
}
