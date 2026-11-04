import { useEffect, useState } from "react";
import { api } from "../api/client.js";

const brl = (v) => Number(v).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });

export default function Clientes() {
  const [lista, setLista] = useState([]);
  const [erro, setErro] = useState("");
  useEffect(() => { api.listarClientes().then(setLista).catch((e) => setErro(e.message)); }, []);
  return (
    <section className="card">
      <h2>Clientes cadastrados</h2>
      {erro && <p className="error">{erro}</p>}
      <div className="table-wrap"><table>
        <thead><tr><th>Nome</th><th>CPF</th><th>RG</th><th>Profissão</th><th>Endereço</th><th>Rendimentos</th></tr></thead>
        <tbody>{lista.map((c) => (
          <tr key={c.id}><td>{c.nome}</td><td>{c.cpf}</td><td>{c.rg}</td><td>{c.profissao}</td><td>{c.endereco}</td>
            <td>{c.rendimentos.map((r) => `${r.entidadeEmpregadora}: ${brl(r.valor)}`).join("; ") || "—"}</td></tr>
        ))}</tbody>
      </table></div>
    </section>
  );
}
