package br.com.aluguelcarros.service;

import br.com.aluguelcarros.dto.AutomovelDtos.*;
import br.com.aluguelcarros.exception.BusinessException;
import br.com.aluguelcarros.exception.NotFoundException;
import br.com.aluguelcarros.model.Automovel;
import br.com.aluguelcarros.repository.AutomovelRepository;
import br.com.aluguelcarros.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutomovelService {
    private final AutomovelRepository repository;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public List<AutomovelResponse> listar() { return repository.findAll().stream().map(AutomovelResponse::of).toList(); }

    public Automovel obter(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Automóvel não encontrado"));
    }

    @Transactional
    public AutomovelResponse criar(AutomovelRequest req) {
        String placa = normalizarPlaca(req.placa());
        if (repository.existsByPlaca(placa)) throw new BusinessException("Placa já cadastrada");
        if (repository.existsByMatricula(req.matricula().trim())) throw new BusinessException("Matrícula já cadastrada");
        Automovel a = new Automovel();
        preencher(a, req, placa);
        return AutomovelResponse.of(repository.save(a));
    }

    @Transactional
    public AutomovelResponse atualizar(Long id, AutomovelRequest req) {
        Automovel a = obter(id);
        String placa = normalizarPlaca(req.placa());
        if (repository.existsByPlacaAndIdNot(placa, id)) throw new BusinessException("Placa já cadastrada");
        if (repository.existsByMatriculaAndIdNot(req.matricula().trim(), id)) throw new BusinessException("Matrícula já cadastrada");
        preencher(a, req, placa);
        return AutomovelResponse.of(a);
    }

    @Transactional
    public void excluir(Long id) {
        if (pedidoRepository.existsByAutomovelId(id)) throw new BusinessException("Automóvel vinculado a pedidos não pode ser excluído");
        repository.delete(obter(id));
    }

    private void preencher(Automovel a, AutomovelRequest req, String placa) {
        a.setMatricula(req.matricula().trim());
        a.setAno(req.ano());
        a.setMarca(req.marca().trim());
        a.setModelo(req.modelo().trim());
        a.setPlaca(placa);
    }

    private static String normalizarPlaca(String p) { return p.replace("-", "").toUpperCase(); }
}
