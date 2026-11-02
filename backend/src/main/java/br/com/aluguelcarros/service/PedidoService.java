package br.com.aluguelcarros.service;

import br.com.aluguelcarros.dto.PedidoDtos.*;
import br.com.aluguelcarros.exception.BusinessException;
import br.com.aluguelcarros.exception.NotFoundException;
import br.com.aluguelcarros.model.*;
import br.com.aluguelcarros.repository.AgenteRepository;
import br.com.aluguelcarros.repository.ContratoRepository;
import br.com.aluguelcarros.repository.PedidoRepository;
import br.com.aluguelcarros.security.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Subsistema de gestão de pedidos e contratos. */
@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ContratoRepository contratoRepository;
    private final AgenteRepository agenteRepository;
    private final ClienteService clienteService;
    private final AutomovelService automovelService;

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(UsuarioAutenticado u) {
        List<Pedido> pedidos = u.getPerfil() == Perfil.CLIENTE
                ? pedidoRepository.findByClienteIdOrderByCriadoEmDesc(u.getId())
                : pedidoRepository.findAllByOrderByCriadoEmDesc();
        return pedidos.stream().map(this::resposta).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(Long id, UsuarioAutenticado u) { return resposta(obterAutorizado(id, u)); }

    @Transactional
    public PedidoResponse criar(PedidoRequest req, UsuarioAutenticado u) {
        validarDatas(req);
        Pedido p = new Pedido();
        p.setCliente(clienteService.obter(u.getId()));
        preencher(p, req);
        return resposta(pedidoRepository.save(p));
    }

    /** Cliente modifica apenas pedidos pendentes; agente pode modificar pedidos ainda não contratados (volta a PENDENTE se cliente alterar). */
    @Transactional
    public PedidoResponse atualizar(Long id, PedidoRequest req, UsuarioAutenticado u) {
        Pedido p = obterAutorizado(id, u);
        validarDatas(req);
        if (u.getPerfil() == Perfil.CLIENTE && !p.isEditavelPeloCliente())
            throw new BusinessException("Somente pedidos pendentes podem ser modificados pelo cliente");
        if (p.getStatus() == StatusPedido.CONTRATADO || p.getStatus() == StatusPedido.CANCELADO)
            throw new BusinessException("Pedido " + p.getStatus().name().toLowerCase() + " não pode ser modificado");
        preencher(p, req);
        return resposta(p);
    }

    @Transactional
    public PedidoResponse cancelar(Long id, UsuarioAutenticado u) {
        Pedido p = obterAutorizado(id, u);
        if (u.getPerfil() != Perfil.CLIENTE) throw new AccessDeniedException("Apenas o cliente pode cancelar");
        if (!p.isCancelavel()) throw new BusinessException("Pedido não pode mais ser cancelado");
        p.setStatus(StatusPedido.CANCELADO);
        return resposta(p);
    }

    /** Análise financeira pelo agente. */
    @Transactional
    public PedidoResponse avaliar(Long id, AvaliacaoRequest req, UsuarioAutenticado u) {
        Pedido p = obter(id);
        if (p.getStatus() != StatusPedido.PENDENTE) throw new BusinessException("Apenas pedidos pendentes podem ser avaliados");
        p.setAgenteAvaliador(agente(u));
        p.setParecer(req.parecer().trim());
        p.setStatus(req.aprovado() ? StatusPedido.APROVADO : StatusPedido.REPROVADO);
        return resposta(p);
    }

    /** Execução do contrato após parecer positivo. Contrato de crédito exige agente do tipo BANCO. */
    @Transactional
    public PedidoResponse executarContrato(Long id, ContratoRequest req, UsuarioAutenticado u) {
        Pedido p = obter(id);
        if (p.getStatus() != StatusPedido.APROVADO) throw new BusinessException("Apenas pedidos aprovados podem ser contratados");
        Agente agente = agente(u);
        if (req.tipoProprietario() == TipoProprietario.BANCO && agente.getTipo() != TipoAgente.BANCO)
            throw new BusinessException("Somente um banco pode ser registrado como proprietário");
        if (req.tipoProprietario() == TipoProprietario.EMPRESA && agente.getTipo() != TipoAgente.EMPRESA)
            throw new BusinessException("Somente uma empresa pode ser registrada como proprietária");

        Contrato c = new Contrato();
        c.setPedido(p);
        c.setAgente(agente);
        c.setTipoProprietario(req.tipoProprietario());

        if (p.isSolicitaCredito()) {
            if (agente.getTipo() != TipoAgente.BANCO) throw new BusinessException("Pedido com crédito deve ser contratado por um banco agente");
            if (req.valorCredito() == null || req.taxaJurosMensal() == null || req.parcelas() == null)
                throw new BusinessException("Informe valor, taxa e parcelas do contrato de crédito");
            ContratoCredito cc = new ContratoCredito();
            cc.setContrato(c);
            cc.setBanco(agente);
            cc.setValor(req.valorCredito());
            cc.setTaxaJurosMensal(req.taxaJurosMensal());
            cc.setParcelas(req.parcelas());
            c.setContratoCredito(cc);
        }

        Automovel auto = p.getAutomovel();
        auto.setTipoProprietario(req.tipoProprietario());
        auto.setProprietario(req.tipoProprietario() == TipoProprietario.CLIENTE ? p.getCliente() : agente);

        p.setStatus(StatusPedido.CONTRATADO);
        contratoRepository.save(c);
        return PedidoResponse.of(p, c);
    }

    private Agente agente(UsuarioAutenticado u) {
        return agenteRepository.findById(u.getId()).orElseThrow(() -> new AccessDeniedException("Usuário não é agente"));
    }

    private Pedido obter(Long id) {
        return pedidoRepository.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado"));
    }

    private Pedido obterAutorizado(Long id, UsuarioAutenticado u) {
        Pedido p = obter(id);
        if (u.getPerfil() == Perfil.CLIENTE && !p.getCliente().getId().equals(u.getId()))
            throw new NotFoundException("Pedido não encontrado");
        return p;
    }

    private void preencher(Pedido p, PedidoRequest req) {
        p.setAutomovel(automovelService.obter(req.automovelId()));
        p.setDataInicio(req.dataInicio());
        p.setDataFim(req.dataFim());
        p.setValorMensal(req.valorMensal());
        p.setSolicitaCredito(req.solicitaCredito());
    }

    private static void validarDatas(PedidoRequest req) {
        if (!req.dataFim().isAfter(req.dataInicio())) throw new BusinessException("Data final deve ser posterior à data inicial");
    }

    private PedidoResponse resposta(Pedido p) {
        return PedidoResponse.of(p, contratoRepository.findByPedidoId(p.getId()).orElse(null));
    }
}
