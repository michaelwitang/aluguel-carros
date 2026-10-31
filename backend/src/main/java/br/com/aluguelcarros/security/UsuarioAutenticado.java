package br.com.aluguelcarros.security;

import br.com.aluguelcarros.model.Agente;
import br.com.aluguelcarros.model.Perfil;
import br.com.aluguelcarros.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Adaptador entre a entidade Usuario e o Spring Security. */
public class UsuarioAutenticado implements UserDetails {
    private final Long id;
    private final String username;
    private final String senha;
    private final Perfil perfil;
    private final List<GrantedAuthority> authorities = new ArrayList<>();

    public UsuarioAutenticado(Usuario u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.senha = u.getSenha();
        this.perfil = u.getPerfil();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + u.getPerfil().name()));
        if (u instanceof Agente a) authorities.add(new SimpleGrantedAuthority("ROLE_" + a.getTipo().name()));
    }

    public Long getId() { return id; }
    public Perfil getPerfil() { return perfil; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return senha; }
    @Override public String getUsername() { return username; }
}
