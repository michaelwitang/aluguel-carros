package br.com.aluguelcarros.repository;

import br.com.aluguelcarros.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByRg(String rg);
    boolean existsByCpfAndIdNot(String cpf, Long id);
    boolean existsByRgAndIdNot(String rg, Long id);
}
