package br.com.aluguelcarros.repository;

import br.com.aluguelcarros.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByCriadoEmDesc(Long clienteId);
    List<Pedido> findAllByOrderByCriadoEmDesc();
    boolean existsByClienteId(Long clienteId);
    boolean existsByAutomovelId(Long automovelId);
}
