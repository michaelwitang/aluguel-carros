package br.com.aluguelcarros.config;

import br.com.aluguelcarros.model.*;
import br.com.aluguelcarros.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Carga inicial de dados de demonstração. Executa apenas quando o banco
 * não possui usuários. Desative com APP_SEED_ENABLED=false.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {
    public static final String SENHA_PADRAO = "senha123";

    private final UsuarioRepository usuarioRepository;
    private final AgenteRepository agenteRepository;
    private final ClienteRepository clienteRepository;
    private final AutomovelRepository automovelRepository;
    private final PedidoRepository pedidoRepository;
    private final ContratoRepository contratoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.findByUsername("locadora").isPresent()) {
            log.info("Carga inicial ignorada: dados de demonstração já existem.");
            return;
        }

        Agente locadora = agente("locadora", "Locadora Rápida LTDA", "12345678000190", TipoAgente.EMPRESA);
        Agente banco = agente("banco", "Banco Horizonte S.A.", "98765432000110", TipoAgente.BANCO);

        Cliente joao = cliente("joao", "João da Silva", "MG1234567", "52998224725",
                "Rua das Flores, 100 - Belo Horizonte/MG", "Engenheiro de Software",
                List.of(rend("Tech Minas LTDA", "8500.00"), rend("Freelancer", "1500.00")));
        Cliente maria = cliente("maria", "Maria Oliveira", "MG7654321", "11144477735",
                "Av. Afonso Pena, 2000 - Belo Horizonte/MG", "Médica",
                List.of(rend("Hospital Central", "15000.00")));
        Cliente pedro = cliente("pedro", "Pedro Santos", "SP9988776", "39053344705",
                "Rua Augusta, 500 - São Paulo/SP", "Professor",
                List.of(rend("Escola Estadual Paulista", "4200.00"), rend("Curso Online", "800.00"), rend("Aulas Particulares", "600.00")));

        Automovel onix = carro("MAT-0001", 2023, "Chevrolet", "Onix", "ABC1D23", TipoProprietario.EMPRESA, locadora);
        Automovel hb20 = carro("MAT-0002", 2022, "Hyundai", "HB20", "DEF4G56", TipoProprietario.EMPRESA, locadora);
        Automovel corolla = carro("MAT-0003", 2024, "Toyota", "Corolla", "GHI7J89", TipoProprietario.BANCO, banco);
        Automovel compass = carro("MAT-0004", 2023, "Jeep", "Compass", "JKL0M12", TipoProprietario.EMPRESA, locadora);
        Automovel civic = carro("MAT-0005", 2021, "Honda", "Civic", "MNO3P45", TipoProprietario.EMPRESA, locadora);
        Automovel polo = carro("MAT-0006", 2024, "Volkswagen", "Polo", "QRS6T78", TipoProprietario.EMPRESA, locadora);
        Automovel kwid = carro("MAT-0007", 2022, "Renault", "Kwid", "UVW9X01", TipoProprietario.EMPRESA, locadora);
        carro("MAT-0008", 2025, "Fiat", "Pulse", "YZA2B34", TipoProprietario.EMPRESA, locadora);

        LocalDate hoje = LocalDate.now();
        pedido(joao, onix, hoje.plusDays(5), hoje.plusMonths(6), "1800.00", false, StatusPedido.PENDENTE, null, null);
        pedido(joao, compass, hoje.plusDays(10), hoje.plusMonths(12), "3500.00", true, StatusPedido.APROVADO, banco,
                "Renda compatível. Crédito pré-aprovado.");
        pedido(maria, hb20, hoje.plusDays(3), hoje.plusMonths(3), "1600.00", false, StatusPedido.PENDENTE, null, null);
        Pedido contratado = pedido(maria, corolla, hoje.minusDays(20), hoje.plusMonths(24), "4200.00", true,
                StatusPedido.CONTRATADO, banco, "Aprovado com financiamento.");
        pedido(pedro, civic, hoje.plusDays(7), hoje.plusMonths(12), "3000.00", false, StatusPedido.REPROVADO, locadora,
                "Rendimentos insuficientes para o valor mensal.");
        pedido(pedro, kwid, hoje.plusDays(2), hoje.plusMonths(2), "1200.00", false, StatusPedido.CANCELADO, null, null);
        pedido(pedro, polo, hoje.plusDays(15), hoje.plusMonths(6), "1700.00", false, StatusPedido.PENDENTE, null, null);

        Contrato contrato = new Contrato();
        contrato.setPedido(contratado);
        contrato.setAgente(banco);
        contrato.setTipoProprietario(TipoProprietario.BANCO);
        ContratoCredito credito = new ContratoCredito();
        credito.setContrato(contrato);
        credito.setBanco(banco);
        credito.setValor(new BigDecimal("100800.00"));
        credito.setTaxaJurosMensal(new BigDecimal("1.49"));
        credito.setParcelas(24);
        contrato.setContratoCredito(credito);
        contratoRepository.save(contrato);

        log.info("Carga inicial concluída: 2 agentes, 3 clientes, 8 automóveis, 7 pedidos e 1 contrato. Senha padrão: {}", SENHA_PADRAO);
    }

    private Agente agente(String user, String nome, String cnpj, TipoAgente tipo) {
        Agente a = new Agente();
        a.setUsername(user);
        a.setSenha(passwordEncoder.encode(SENHA_PADRAO));
        a.setNome(nome);
        a.setCnpj(cnpj);
        a.setTipo(tipo);
        a.setPerfil(Perfil.AGENTE);
        return agenteRepository.save(a);
    }

    private Cliente cliente(String user, String nome, String rg, String cpf, String endereco, String profissao, List<Rendimento> rends) {
        Cliente c = new Cliente();
        c.setUsername(user);
        c.setSenha(passwordEncoder.encode(SENHA_PADRAO));
        c.setNome(nome);
        c.setRg(rg);
        c.setCpf(cpf);
        c.setEndereco(endereco);
        c.setProfissao(profissao);
        c.setPerfil(Perfil.CLIENTE);
        c.substituirRendimentos(rends);
        return clienteRepository.save(c);
    }

    private static Rendimento rend(String entidade, String valor) {
        Rendimento r = new Rendimento();
        r.setEntidadeEmpregadora(entidade);
        r.setValor(new BigDecimal(valor));
        return r;
    }

    private Automovel carro(String matricula, int ano, String marca, String modelo, String placa, TipoProprietario tipo, Usuario dono) {
        Automovel a = new Automovel();
        a.setMatricula(matricula);
        a.setAno(ano);
        a.setMarca(marca);
        a.setModelo(modelo);
        a.setPlaca(placa);
        a.setTipoProprietario(tipo);
        a.setProprietario(dono);
        return automovelRepository.save(a);
    }

    private Pedido pedido(Cliente c, Automovel a, LocalDate ini, LocalDate fim, String valor, boolean credito,
                          StatusPedido status, Agente avaliador, String parecer) {
        Pedido p = new Pedido();
        p.setCliente(c);
        p.setAutomovel(a);
        p.setDataInicio(ini);
        p.setDataFim(fim);
        p.setValorMensal(new BigDecimal(valor));
        p.setSolicitaCredito(credito);
        p.setStatus(status);
        p.setAgenteAvaliador(avaliador);
        p.setParecer(parecer);
        return pedidoRepository.save(p);
    }
}
