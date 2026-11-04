import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const sair = () => { logout(); navigate("/login"); };
  return (
    <div className="app">
      <header className="topbar">
        <span className="brand">AutoRent</span>
        <nav>
          <NavLink to="/pedidos">Pedidos</NavLink>
          <NavLink to="/automoveis">Automóveis</NavLink>
          {user.perfil === "CLIENTE" && <NavLink to="/perfil">Meu cadastro</NavLink>}
          {user.perfil === "AGENTE" && <NavLink to="/clientes">Clientes</NavLink>}
        </nav>
        <div className="user">
          <span>{user.nome} · {user.perfil === "AGENTE" ? (user.tipoAgente === "BANCO" ? "Banco" : "Empresa") : "Cliente"}</span>
          <button className="btn ghost" onClick={sair}>Sair</button>
        </div>
      </header>
      <main className="container"><Outlet /></main>
    </div>
  );
}
