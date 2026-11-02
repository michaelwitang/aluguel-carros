package br.com.aluguelcarros.controller;

import br.com.aluguelcarros.dto.AutomovelDtos.*;
import br.com.aluguelcarros.service.AutomovelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/automoveis")
@RequiredArgsConstructor
public class AutomovelController {
    private final AutomovelService service;

    @GetMapping
    public List<AutomovelResponse> listar() { return service.listar(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('AGENTE')")
    public AutomovelResponse criar(@Valid @RequestBody AutomovelRequest req) { return service.criar(req); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AGENTE')")
    public AutomovelResponse atualizar(@PathVariable Long id, @Valid @RequestBody AutomovelRequest req) { return service.atualizar(id, req); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('AGENTE')")
    public void excluir(@PathVariable Long id) { service.excluir(id); }
}
