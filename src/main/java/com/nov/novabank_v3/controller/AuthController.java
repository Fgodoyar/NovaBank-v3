package com.nov.novabank_v3.controller;

import com.nov.novabank_v3.dto.LoginRequestDTO;
import com.nov.novabank_v3.dto.LoginResponseDTO;
import com.nov.novabank_v3.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticación y generación de tokens JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Autentica con username/password y devuelve un token JWT Bearer. " +
                    "Credenciales por defecto: admin / password"
    )
    @ApiResponse(responseCode = "200", description = "Token generado correctamente")
    @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtService.generateToken(authentication.getName());

        return ResponseEntity.ok(new LoginResponseDTO(token, "Bearer", jwtService.getExpiration()));
    }
}