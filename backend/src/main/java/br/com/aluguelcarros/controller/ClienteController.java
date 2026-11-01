package br.com.aluguelcarros.controller;

import br.com.aluguelcarros.dto.ClienteDtos.*;
import br.com.aluguelcarros.security.UsuarioAutenticado;
import br.com.aluguelcarros.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** CRUD de cliente. Criação é pública (cadastro prévio); demais operações exigem login. */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse criar(@Valid @RequestBody ClienteRequest req) { return service.criar(req); }

    @GetMapping
    @PreAuthorize("hasRole('AGENTE')")
    public List<ClienteResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado u) {
        autorizar(id, u, true);
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest req,
                                     @AuthenticationPrincipal UsuarioAutenticado u) {
        autorizar(id, u, false);
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado u) {
        autorizar(id, u, false);
        service.excluir(id);
    }

    /** O próprio cliente gerencia seus dados; agentes podem apenas consultar. */
    private static void autorizar(Long id, UsuarioAutenticado u, boolean leitura) {
        boolean dono = u.getId().equals(id);
        boolean agenteLendo = leitura && u.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_AGENTE"));
        if (!dono && !agenteLendo) throw new AccessDeniedException("Acesso negado");
    }
}
