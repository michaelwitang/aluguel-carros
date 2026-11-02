package br.com.aluguelcarros.controller;

import br.com.aluguelcarros.dto.PedidoDtos.*;
import br.com.aluguelcarros.security.UsuarioAutenticado;
import br.com.aluguelcarros.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService service;

    @GetMapping
    public List<PedidoResponse> listar(@AuthenticationPrincipal UsuarioAutenticado u) { return service.listar(u); }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado u) { return service.buscar(id, u); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse criar(@Valid @RequestBody PedidoRequest req, @AuthenticationPrincipal UsuarioAutenticado u) { return service.criar(req, u); }

    @PutMapping("/{id}")
    public PedidoResponse atualizar(@PathVariable Long id, @Valid @RequestBody PedidoRequest req,
                                    @AuthenticationPrincipal UsuarioAutenticado u) { return service.atualizar(id, req, u); }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse cancelar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado u) { return service.cancelar(id, u); }

    @PatchMapping("/{id}/avaliar")
    @PreAuthorize("hasRole('AGENTE')")
    public PedidoResponse avaliar(@PathVariable Long id, @Valid @RequestBody AvaliacaoRequest req,
                                  @AuthenticationPrincipal UsuarioAutenticado u) { return service.avaliar(id, req, u); }

    @PostMapping("/{id}/contrato")
    @PreAuthorize("hasRole('AGENTE')")
    public PedidoResponse contratar(@PathVariable Long id, @Valid @RequestBody ContratoRequest req,
                                    @AuthenticationPrincipal UsuarioAutenticado u) { return service.executarContrato(id, req, u); }
}
