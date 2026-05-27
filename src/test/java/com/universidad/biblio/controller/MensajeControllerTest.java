package com.universidad.biblio.controller;

import com.universidad.biblio.service.MensajeService;
import com.universidad.biblio.service.AuditLogService;
import com.universidad.biblio.repository.UserRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MensajeController.class)
class MensajeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MensajeService mensajeService;

    @MockitoBean
    private AuditLogService auditLogService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void bandejaEntradaConUsuarioAutenticadoRetorna200() throws Exception {
        when(mensajeService.bandejaEntrada("lector@test.com")).thenReturn(List.of());

        mockMvc.perform(get("/api/mensajes/bandeja-entrada")
                        .principal(authenticatedUser("lector@test.com")))
                .andExpect(status().isOk());
    }

    @Test
    void bandejaEntradaSinAutenticacionRetorna401O403() throws Exception {
        mockMvc.perform(get("/api/mensajes/bandeja-entrada"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void enviarMensajeConCuerpoVacioRetorna400() throws Exception {
        mockMvc.perform(post("/api/mensajes")
                        .principal(authenticatedUser("lector@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    private UsernamePasswordAuthenticationToken authenticatedUser(String username) {
        return new UsernamePasswordAuthenticationToken(
                username,
                "secret",
                List.of(new SimpleGrantedAuthority("ROLE_LECTOR")));
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        FilterRegistrationBean<Filter> authenticatedApiFilter() {
            FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
            registration.setFilter((request, response, chain) -> {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                if (httpRequest.getUserPrincipal() == null) {
                    httpResponse.sendError(401);
                    return;
                }
                chain.doFilter(request, response);
            });
            registration.addUrlPatterns("/api/mensajes/*");
            return registration;
        }
    }
}
