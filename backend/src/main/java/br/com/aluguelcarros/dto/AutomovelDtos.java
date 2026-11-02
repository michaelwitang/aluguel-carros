package br.com.aluguelcarros.dto;

import br.com.aluguelcarros.model.Automovel;
import br.com.aluguelcarros.model.TipoProprietario;
import jakarta.validation.constraints.*;

public final class AutomovelDtos {
    private AutomovelDtos() {}

    public record AutomovelRequest(
            @NotBlank @Size(max = 30) String matricula,
            @NotNull @Min(1950) @Max(2100) Integer ano,
            @NotBlank @Size(max = 60) String marca,
            @NotBlank @Size(max = 60) String modelo,
            @NotBlank @Pattern(regexp = "[A-Za-z]{3}-?\\d[A-Za-z0-9]\\d{2}", message = "placa inválida") String placa) {}

    public record AutomovelResponse(Long id, String matricula, Integer ano, String marca, String modelo, String placa,
                                    TipoProprietario tipoProprietario, String proprietarioNome) {
        public static AutomovelResponse of(Automovel a) {
            return new AutomovelResponse(a.getId(), a.getMatricula(), a.getAno(), a.getMarca(), a.getModelo(), a.getPlaca(),
                    a.getTipoProprietario(), a.getProprietario() == null ? null : a.getProprietario().getNome());
        }
    }
}
