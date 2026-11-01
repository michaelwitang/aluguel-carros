package br.com.aluguelcarros.service;

import br.com.aluguelcarros.dto.ClienteDtos.*;
import br.com.aluguelcarros.exception.BusinessException;
import br.com.aluguelcarros.exception.NotFoundException;
import br.com.aluguelcarros.model.Cliente;
import br.com.aluguelcarros.model.Perfil;
import br.com.aluguelcarros.model.Rendimento;
import br.com.aluguelcarros.repository.ClienteRepository;
import br.com.aluguelcarros.repository.PedidoRepository;
import br.com.aluguelcarros.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream().map(ClienteResponse::of).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id) { return ClienteResponse.of(obter(id)); }

    public Cliente obter(Long id) {
        return clienteRepository.findById(id).orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest req) {
        if (req.senha() == null || req.senha().isBlank()) throw new BusinessException("Senha é obrigatória");
        if (usuarioRepository.existsByUsername(req.username())) throw new BusinessException("Nome de usuário já utilizado");
        String cpf = digitos(req.cpf());
        if (clienteRepository.existsByCpf(cpf)) throw new BusinessException("CPF já cadastrado");
        if (clienteRepository.existsByRg(req.rg().trim())) throw new BusinessException("RG já cadastrado");
        Cliente c = new Cliente();
        c.setUsername(req.username().trim());
        c.setSenha(passwordEncoder.encode(req.senha()));
        c.setPerfil(Perfil.CLIENTE);
        preencher(c, req, cpf);
        return ClienteResponse.of(clienteRepository.save(c));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest req) {
        Cliente c = obter(id);
        String cpf = digitos(req.cpf());
        if (clienteRepository.existsByCpfAndIdNot(cpf, id)) throw new BusinessException("CPF já cadastrado");
        if (clienteRepository.existsByRgAndIdNot(req.rg().trim(), id)) throw new BusinessException("RG já cadastrado");
        if (req.senha() != null && !req.senha().isBlank()) c.setSenha(passwordEncoder.encode(req.senha()));
        preencher(c, req, cpf);
        return ClienteResponse.of(c);
    }

    @Transactional
    public void excluir(Long id) {
        Cliente c = obter(id);
        if (pedidoRepository.existsByClienteId(id)) {
            throw new BusinessException("Cliente possui pedidos registrados e não pode ser excluído");
        }
        clienteRepository.delete(c);
    }

    private void preencher(Cliente c, ClienteRequest req, String cpf) {
        c.setNome(req.nome().trim());
        c.setRg(req.rg().trim());
        c.setCpf(cpf);
        c.setEndereco(req.endereco().trim());
        c.setProfissao(req.profissao().trim());
        c.substituirRendimentos(req.rendimentos().stream().map(r -> {
            Rendimento rend = new Rendimento();
            rend.setEntidadeEmpregadora(r.entidadeEmpregadora().trim());
            rend.setValor(r.valor());
            return rend;
        }).toList());
    }

    private static String digitos(String s) { return s.replaceAll("\\D", ""); }
}
