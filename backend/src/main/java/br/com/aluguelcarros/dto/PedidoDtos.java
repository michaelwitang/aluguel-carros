package br.com.aluguelcarros.dto;

import br.com.aluguelcarros.model.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class PedidoDtos {
    private PedidoDtos() {}

    public record PedidoRequest(@NotNull Long automovelId,
                                @NotNull @FutureOrPresent LocalDate dataInicio,
                                @NotNull LocalDate dataFim,
                                @NotNull @DecimalMin("0.01") BigDecimal valorMensal,
                                boolean solicitaCredito) {}

    public record AvaliacaoRequest(@NotNull Boolean aprovado, @NotBlank @Size(max = 500) String parecer) {}

    public record ContratoRequest(@NotNull TipoProprietario tipoProprietario,
                                  @DecimalMin("0.01") BigDecimal valorCredito,
                                  @DecimalMin("0.0") BigDecimal taxaJurosMensal,
                                  @Min(1) @Max(120) Integer parcelas) {}

    public record ContratoResponse(Long id, String agente, TipoProprietario tipoProprietario, LocalDateTime dataAssinatura,
                                   String banco, BigDecimal valorCredito, BigDecimal taxaJurosMensal, Integer parcelas) {
        public static ContratoResponse of(Contrato c) {
            ContratoCredito cc = c.getContratoCredito();
            return new ContratoResponse(c.getId(), c.getAgente().getNome(), c.getTipoProprietario(), c.getDataAssinatura(),
                    cc == null ? null : cc.getBanco().getNome(), cc == null ? null : cc.getValor(),
                    cc == null ? null : cc.getTaxaJurosMensal(), cc == null ? null : cc.getParcelas());
        }
    }

    public record PedidoResponse(Long id, Long clienteId, String clienteNome, String clienteCpf, BigDecimal rendaTotal,
                                 AutomovelDtos.AutomovelResponse automovel, LocalDate dataInicio, LocalDate dataFim,
                                 BigDecimal valorMensal, boolean solicitaCredito, StatusPedido status,
                                 String agenteAvaliador, String parecer, LocalDateTime criadoEm, ContratoResponse contrato) {
        public static PedidoResponse of(Pedido p, Contrato contrato) {
            BigDecimal renda = p.getCliente().getRendimentos().stream().map(Rendimento::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
            return new PedidoResponse(p.getId(), p.getCliente().getId(), p.getCliente().getNome(), p.getCliente().getCpf(), renda,
                    AutomovelDtos.AutomovelResponse.of(p.getAutomovel()), p.getDataInicio(), p.getDataFim(), p.getValorMensal(),
                    p.isSolicitaCredito(), p.getStatus(),
                    p.getAgenteAvaliador() == null ? null : p.getAgenteAvaliador().getNome(), p.getParecer(), p.getCriadoEm(),
                    contrato == null ? null : ContratoResponse.of(contrato));
        }
    }
}
