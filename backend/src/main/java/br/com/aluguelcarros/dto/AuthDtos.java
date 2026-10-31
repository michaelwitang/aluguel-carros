package br.com.aluguelcarros.dto;

import br.com.aluguelcarros.model.Perfil;
import br.com.aluguelcarros.model.TipoAgente;
import jakarta.validation.constraints.*;

public final class AuthDtos {
    private AuthDtos() {}

    public record LoginRequest(@NotBlank String username, @NotBlank String senha) {}

    public record AuthResponse(String token, Long id, String username, String nome, Perfil perfil, TipoAgente tipoAgente) {}

    public record AgenteRequest(
            @NotBlank @Size(min = 3, max = 60) String username,
            @NotBlank @Size(min = 6, max = 100) String senha,
            @NotBlank @Size(max = 120) String nome,
            @NotBlank @Pattern(regexp = "\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}", message = "CNPJ inválido") String cnpj,
            @NotNull TipoAgente tipo) {}
}
