package br.com.aluguelcarros.repository;

import br.com.aluguelcarros.model.Agente;
import br.com.aluguelcarros.model.TipoAgente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgenteRepository extends JpaRepository<Agente, Long> {
    boolean existsByCnpj(String cnpj);
    List<Agente> findByTipo(TipoAgente tipo);
}
