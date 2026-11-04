import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api/client.js";
import { useAuth } from "../context/AuthContext.jsx";
import ClienteForm from "../components/ClienteForm.jsx";

function AgenteForm({ onDone }) {
  const [f, setF] = useState({ username: "", senha: "", nome: "", cnpj: "", tipo: "EMPRESA" });
  const [erro, setErro] = useState("");
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const enviar = async (e) => {
    e.preventDefault(); setErro("");
    try { onDone(await api.registrarAgente(f)); } catch (err) { setErro(err.message); }
  };
  return (
    <form className="form" onSubmit={enviar}>
      <div className="grid2">
        <label>Usuário<input value={f.username} onChange={set("username")} required minLength={3} /></label>
        <label>Senha<input type="password" value={f.senha} onChange={set("senha")} required minLength={6} /></label>
        <label className="span2">Razão social<input value={f.nome} onChange={set("nome")} required /></label>
        <label>CNPJ<input value={f.cnpj} onChange={set("cnpj")} required placeholder="00.000.000/0000-00" /></label>
        <label>Tipo<select value={f.tipo} onChange={set("tipo")}><option value="EMPRESA">Empresa</option><option value="BANCO">Banco</option></select></label>
      </div>
      {erro && <p className="error">{erro}</p>}
      <button className="btn">Cadastrar agente</button>
    </form>
  );
}

export default function Cadastro() {
  const [tipo, setTipo] = useState("CLIENTE");
  const { login, entrar } = useAuth();
  const navigate = useNavigate();

  const cadastrarCliente = async (dados) => {
    await api.criarCliente(dados);
    await login(dados.username, dados.senha);
    navigate("/pedidos");
  };

  return (
    <div className="auth">
      <div className="card wide">
        <h1 className="brand">Criar conta</h1>
        <div className="tabs">
          <button className={tipo === "CLIENTE" ? "active" : ""} onClick={() => setTipo("CLIENTE")}>Cliente</button>
          <button className={tipo === "AGENTE" ? "active" : ""} onClick={() => setTipo("AGENTE")}>Agente (empresa/banco)</button>
        </div>
        {tipo === "CLIENTE"
          ? <ClienteForm onSubmit={cadastrarCliente} submitLabel="Cadastrar cliente" />
          : <AgenteForm onDone={(r) => { entrar(r); navigate("/pedidos"); }} />}
        <p className="muted">Já possui conta? <Link to="/login">Entrar</Link></p>
      </div>
    </div>
  );
}
