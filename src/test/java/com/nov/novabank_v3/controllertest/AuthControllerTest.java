package com.nov.novabank_v3.controllertest;

import com.nov.novabank_v3.config.SecurityConfig;
import com.nov.novabank_v3.controller.AuthController;
import com.nov.novabank_v3.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private static final String VALID_JSON = """
            {
              "username": "admin",
              "password": "password"
            }
            """;

    @Nested
    class loginTest {

        @Test
        @DisplayName("POST /api/auth/login → 200 con token generado")
        void login_shouldReturn200WhenValid() throws Exception {
            Authentication auth = mock(Authentication.class);
            when(auth.getName()).thenReturn("admin");
            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(jwtService.generateToken("admin")).thenReturn("token.jwt.fake");
            when(jwtService.getExpiration()).thenReturn(86400000L);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("token.jwt.fake"))
                    .andExpect(jsonPath("$.tipo").value("Bearer"))
                    .andExpect(jsonPath("$.expiration").value(86400000L));
        }

        @Test
        @DisplayName("POST /api/auth/login → 401 con credenciales incorrectas")
        void login_shouldReturn401WhenInvalidCredentials() throws Exception {
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/auth/login → 400 si el body no supera la validación @Valid")
        void login_shouldReturn400WhenInvalid() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(authenticationManager);
        }
    }
}