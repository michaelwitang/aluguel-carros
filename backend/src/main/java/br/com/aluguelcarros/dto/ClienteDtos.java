package br.com.aluguelcarros.dto;

import br.com.aluguelcarros.model.Cliente;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public final class ClienteDtos {
    private ClienteDtos() {}

    public record RendimentoDto(@NotBlank @Size(max = 120) String entidadeEmpregadora,
                                @NotNull @DecimalMin("0.0") BigDecimal valor) {}

    /** senha é obrigatória na criação e opcional na atualização. username é ignorado na atualização. */
    public record ClienteRequest(
            @NotBlank @Size(min = 3, max = 60) String username,
            @Size(min = 6, max = 100) String senha,
            @NotBlank @Size(max = 120) String nome,
            @NotBlank @Size(max = 20) String rg,
            @NotBlank @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}", message = "CPF inválido") String cpf,
            @NotBlank String endereco,
            @NotBlank @Size(max = 80) String profissao,
            @NotNull @Size(max = 3, message = "máximo de 3 rendimentos") List<@Valid RendimentoDto> rendimentos) {}

    public record ClienteResponse(Long id, String username, String nome, String rg, String cpf,
                                  String endereco, String profissao, List<RendimentoDto> rendimentos) {
        public static ClienteResponse of(Cliente c) {
            return new ClienteResponse(c.getId(), c.getUsername(), c.getNome(), c.getRg(), c.getCpf(),
                    c.getEndereco(), c.getProfissao(),
                    c.getRendimentos().stream().map(r -> new RendimentoDto(r.getEntidadeEmpregadora(), r.getValor())).toList());
        }
    }
}
