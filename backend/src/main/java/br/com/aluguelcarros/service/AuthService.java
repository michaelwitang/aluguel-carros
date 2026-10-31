package br.com.aluguelcarros.service;

import br.com.aluguelcarros.dto.AuthDtos.*;
import br.com.aluguelcarros.exception.BusinessException;
import br.com.aluguelcarros.model.*;
import br.com.aluguelcarros.repository.AgenteRepository;
import br.com.aluguelcarros.repository.UsuarioRepository;
import br.com.aluguelcarros.security.JwtService;
import br.com.aluguelcarros.security.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final AgenteRepository agenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.senha()));
        Usuario u = usuarioRepository.findByUsername(req.username()).orElseThrow();
        return resposta(u);
    }

    @Transactional(readOnly = true)
    public AuthResponse me(Long id) {
        return resposta(usuarioRepository.findById(id).orElseThrow());
    }

    @Transactional
    public AuthResponse registrarAgente(AgenteRequest req) {
        if (usuarioRepository.existsByUsername(req.username())) throw new BusinessException("Nome de usuário já utilizado");
        String cnpj = req.cnpj().replaceAll("\\D", "");
        if (agenteRepository.existsByCnpj(cnpj)) throw new BusinessException("CNPJ já cadastrado");
        Agente a = new Agente();
        a.setUsername(req.username().trim());
        a.setSenha(passwordEncoder.encode(req.senha()));
        a.setNome(req.nome().trim());
        a.setCnpj(cnpj);
        a.setTipo(req.tipo());
        a.setPerfil(Perfil.AGENTE);
        return resposta(agenteRepository.save(a));
    }

    private AuthResponse resposta(Usuario u) {
        TipoAgente tipo = u instanceof Agente a ? a.getTipo() : null;
        return new AuthResponse(jwtService.gerarToken(new UsuarioAutenticado(u)), u.getId(), u.getUsername(), u.getNome(), u.getPerfil(), tipo);
    }
}
