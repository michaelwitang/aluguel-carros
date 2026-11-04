import { useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

export default function Login() {
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  if (user) return <Navigate to="/pedidos" replace />;

  const enviar = async (e) => {
    e.preventDefault(); setErro("");
    try { await login(username, senha); navigate("/pedidos"); } catch (err) { setErro(err.message); }
  };

  return (
    <div className="auth">
      <form className="card form" onSubmit={enviar}>
        <h1 className="brand">AutoRent</h1>
        <p className="muted">Acesse com seu usuário e senha</p>
        <label>Usuário<input value={username} onChange={(e) => setUsername(e.target.value)} required autoFocus /></label>
        <label>Senha<input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} required /></label>
        {erro && <p className="error">{erro}</p>}
        <button className="btn">Entrar</button>
        <p className="muted">Não possui cadastro? <Link to="/cadastro">Cadastre-se</Link></p>
      </form>
    </div>
  );
}
