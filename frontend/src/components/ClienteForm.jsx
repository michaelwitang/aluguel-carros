import { useState } from "react";

const VAZIO = { username: "", senha: "", nome: "", rg: "", cpf: "", endereco: "", profissao: "", rendimentos: [] };

/** Formulário reutilizado no cadastro e na edição do cliente. */
export default function ClienteForm({ inicial, edicao = false, onSubmit, submitLabel }) {
  const [f, setF] = useState({ ...VAZIO, ...inicial, senha: "" });
  const [erro, setErro] = useState("");
  const [enviando, setEnviando] = useState(false);
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const setRend = (i, k, v) => setF({ ...f, rendimentos: f.rendimentos.map((r, j) => (j === i ? { ...r, [k]: v } : r)) });

  const enviar = async (e) => {
    e.preventDefault();
    setErro(""); setEnviando(true);
    try {
      await onSubmit({ ...f, senha: f.senha || null, rendimentos: f.rendimentos.map((r) => ({ ...r, valor: Number(r.valor) })) });
    } catch (err) { setErro(err.message); } finally { setEnviando(false); }
  };

  return (
    <form className="form" onSubmit={enviar}>
      <div className="grid2">
        <label>Usuário<input value={f.username} onChange={set("username")} required minLength={3} disabled={edicao} /></label>
        <label>{edicao ? "Nova senha (opcional)" : "Senha"}<input type="password" value={f.senha} onChange={set("senha")} required={!edicao} minLength={6} /></label>
        <label className="span2">Nome completo<input value={f.nome} onChange={set("nome")} required /></label>
        <label>RG<input value={f.rg} onChange={set("rg")} required /></label>
        <label>CPF<input value={f.cpf} onChange={set("cpf")} required placeholder="000.000.000-00" /></label>
        <label className="span2">Endereço<input value={f.endereco} onChange={set("endereco")} required /></label>
        <label className="span2">Profissão<input value={f.profissao} onChange={set("profissao")} required /></label>
      </div>
      <fieldset>
        <legend>Rendimentos ({f.rendimentos.length}/3)</legend>
        {f.rendimentos.map((r, i) => (
          <div className="row" key={i}>
            <input placeholder="Entidade empregadora" value={r.entidadeEmpregadora} onChange={(e) => setRend(i, "entidadeEmpregadora", e.target.value)} required />
            <input type="number" min="0" step="0.01" placeholder="Valor (R$)" value={r.valor} onChange={(e) => setRend(i, "valor", e.target.value)} required />
            <button type="button" className="btn ghost" onClick={() => setF({ ...f, rendimentos: f.rendimentos.filter((_, j) => j !== i) })}>Remover</button>
          </div>
        ))}
        {f.rendimentos.length < 3 && (
          <button type="button" className="btn ghost" onClick={() => setF({ ...f, rendimentos: [...f.rendimentos, { entidadeEmpregadora: "", valor: "" }] })}>+ Adicionar rendimento</button>
        )}
      </fieldset>
      {erro && <p className="error">{erro}</p>}
      <button className="btn" disabled={enviando}>{enviando ? "Salvando…" : submitLabel}</button>
    </form>
  );
}
