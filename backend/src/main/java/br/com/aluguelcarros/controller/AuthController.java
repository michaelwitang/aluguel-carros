package br.com.aluguelcarros.controller;

import br.com.aluguelcarros.dto.AuthDtos.*;
import br.com.aluguelcarros.security.UsuarioAutenticado;
import br.com.aluguelcarros.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) { return service.login(req); }

    @PostMapping("/registrar/agente")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registrarAgente(@Valid @RequestBody AgenteRequest req) { return service.registrarAgente(req); }

    @GetMapping("/me")
    public AuthResponse me(@AuthenticationPrincipal UsuarioAutenticado u) { return service.me(u.getId()); }
}
