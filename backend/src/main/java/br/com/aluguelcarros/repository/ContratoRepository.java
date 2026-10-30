package br.com.aluguelcarros.repository;

import br.com.aluguelcarros.model.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    Optional<Contrato> findByPedidoId(Long pedidoId);
}
