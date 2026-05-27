package com.universidad.biblio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.biblio.controller.MessageController;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.UserRepository;
import com.universidad.biblio.service.MessageService;
import com.universidad.biblio.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MessageController.class)
public class MensajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    private User authenticatedUser;

    @BeforeEach
    void setUp() {
        authenticatedUser = new User();
        authenticatedUser.setId(1);
        authenticatedUser.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(authenticatedUser));
    }

    @Test
    void getInboxWithAuthenticatedUserShouldReturnOk() throws Exception {
        when(messageService.byReceiver(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/mensajes/bandeja-entrada")
                        .with(user("test@example.com").roles("LECTOR")))
                .andExpect(status().isOk());
    }

    @Test
    void getInboxWithoutAuthenticationShouldReturnClientError() throws Exception {
        mockMvc.perform(get("/api/mensjaes/bandeja-entrada"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void postMessageWithEmptyBodyShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/mensjaes")
                        .contentType("application/json")
                        .content("{}")
                        .with(user("test@example.com").roles("LECTOR")))
                .andExpect(status().isBadRequest());
    }
}
