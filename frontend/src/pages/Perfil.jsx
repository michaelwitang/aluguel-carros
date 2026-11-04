import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/client.js";
import { useAuth } from "../context/AuthContext.jsx";
import ClienteForm from "../components/ClienteForm.jsx";

export default function Perfil() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [cliente, setCliente] = useState(null);
  const [msg, setMsg] = useState("");
  const [erro, setErro] = useState("");

  useEffect(() => { api.buscarCliente(user.id).then(setCliente).catch((e) => setErro(e.message)); }, [user.id]);

  const salvar = async (dados) => { setCliente(await api.atualizarCliente(user.id, dados)); setMsg("Dados atualizados."); };
  const excluir = async () => {
    if (!confirm("Excluir definitivamente sua conta?")) return;
    try { await api.excluirCliente(user.id); logout(); navigate("/login"); } catch (e) { setErro(e.message); }
  };

  if (erro && !cliente) return <p className="error">{erro}</p>;
  if (!cliente) return <p className="muted">Carregando…</p>;
  return (
    <section className="card">
      <h2>Meu cadastro</h2>
      {msg && <p className="success">{msg}</p>}
      <ClienteForm key={JSON.stringify(cliente)} inicial={cliente} edicao onSubmit={salvar} submitLabel="Salvar alterações" />
      <hr />
      {erro && <p className="error">{erro}</p>}
      <button className="btn danger" onClick={excluir}>Excluir conta</button>
    </section>
  );
}
