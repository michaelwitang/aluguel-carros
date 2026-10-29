package br.com.aluguelcarros.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter @Setter
public class Cliente extends Usuario {
    public static final int MAX_RENDIMENTOS = 3;

    @Column(nullable = false, unique = true, length = 20)
    private String rg;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false, length = 80)
    private String profissao;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Rendimento> rendimentos = new ArrayList<>();

    public void substituirRendimentos(List<Rendimento> novos) {
        if (novos.size() > MAX_RENDIMENTOS) {
            throw new IllegalArgumentException("Máximo de " + MAX_RENDIMENTOS + " rendimentos por cliente");
        }
        rendimentos.clear();
        novos.forEach(r -> { r.setCliente(this); rendimentos.add(r); });
    }
}
