import { useEffect, useState } from "react";
import { api } from "../api/client.js";
import { useAuth } from "../context/AuthContext.jsx";

const VAZIO = { matricula: "", ano: new Date().getFullYear(), marca: "", modelo: "", placa: "" };
const PROP = { CLIENTE: "Cliente", EMPRESA: "Empresa", BANCO: "Banco" };

export default function Automoveis() {
  const { user } = useAuth();
  const agente = user.perfil === "AGENTE";
  const [lista, setLista] = useState([]);
  const [form, setForm] = useState(VAZIO);
  const [editId, setEditId] = useState(null);
  const [erro, setErro] = useState("");

  const carregar = () => api.listarAutomoveis().then(setLista).catch((e) => setErro(e.message));
  useEffect(() => { carregar(); }, []);
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const salvar = async (e) => {
    e.preventDefault(); setErro("");
    try {
      const b = { ...form, ano: Number(form.ano) };
      editId ? await api.atualizarAutomovel(editId, b) : await api.criarAutomovel(b);
      setForm(VAZIO); setEditId(null); carregar();
    } catch (err) { setErro(err.message); }
  };
  const excluir = async (id) => {
    if (!confirm("Excluir automóvel?")) return;
    try { await api.excluirAutomovel(id); carregar(); } catch (err) { setErro(err.message); }
  };

  return (
    <>
      {agente && (
        <section className="card">
          <h2>{editId ? "Editar automóvel" : "Novo automóvel"}</h2>
          <form className="form grid5" onSubmit={salvar}>
            <label>Matrícula<input value={form.matricula} onChange={set("matricula")} required /></label>
            <label>Ano<input type="number" value={form.ano} onChange={set("ano")} min="1950" max="2100" required /></label>
            <label>Marca<input value={form.marca} onChange={set("marca")} required /></label>
            <label>Modelo<input value={form.modelo} onChange={set("modelo")} required /></label>
            <label>Placa<input value={form.placa} onChange={set("placa")} required placeholder="ABC1D23" /></label>
            <div className="actions">
              <button className="btn">{editId ? "Salvar" : "Cadastrar"}</button>
              {editId && <button type="button" className="btn ghost" onClick={() => { setEditId(null); setForm(VAZIO); }}>Cancelar</button>}
            </div>
          </form>
        </section>
      )}
      <section className="card">
        <h2>Automóveis</h2>
        {erro && <p className="error">{erro}</p>}
        <div className="table-wrap"><table>
          <thead><tr><th>Matrícula</th><th>Placa</th><th>Marca / Modelo</th><th>Ano</th><th>Proprietário</th>{agente && <th />}</tr></thead>
          <tbody>{lista.map((a) => (
            <tr key={a.id}>
              <td>{a.matricula}</td><td>{a.placa}</td><td>{a.marca} {a.modelo}</td><td>{a.ano}</td>
              <td>{PROP[a.tipoProprietario]}{a.proprietarioNome ? ` · ${a.proprietarioNome}` : ""}</td>
              {agente && <td className="actions">
                <button className="btn ghost" onClick={() => { setEditId(a.id); setForm({ matricula: a.matricula, ano: a.ano, marca: a.marca, modelo: a.modelo, placa: a.placa }); }}>Editar</button>
                <button className="btn ghost danger" onClick={() => excluir(a.id)}>Excluir</button>
              </td>}
            </tr>
          ))}</tbody>
        </table></div>
        {!lista.length && <p className="muted">Nenhum automóvel cadastrado.</p>}
      </section>
    </>
  );
}
