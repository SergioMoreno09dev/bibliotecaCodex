package com.universidad.biblio;

import com.universidad.biblio.model.Solicitud;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.UserRepository;
import com.universidad.biblio.service.SolicitudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SolicitudSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolicitudService solicitudService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void postSolicitudWithoutAuthenticationShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(APPLICATION_JSON)
                        .content("{\"type\":\"soporte\",\"description\":\"Prueba\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void postSolicitudWithAuthenticatedUserShouldReturnCreated() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(solicitudService.create(1, "soporte", "Prueba")).thenReturn(new Solicitud());

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(APPLICATION_JSON)
                        .content("{\"type\":\"soporte\",\"description\":\"Prueba\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void putAprobarSolicitudWithUserRoleShouldReturnForbidden() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(put("/api/solicitudes/1/aprobar")
                        .contentType(APPLICATION_JSON)
                        .content("{\"observation\":\"No autorizado\"}"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(solicitudService);
    }
}
