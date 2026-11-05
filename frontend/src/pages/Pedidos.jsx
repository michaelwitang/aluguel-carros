import { useEffect, useState } from "react";
import { api } from "../api/client.js";
import { useAuth } from "../context/AuthContext.jsx";
import StatusBadge from "../components/StatusBadge.jsx";

const brl = (v) => Number(v).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
const data = (d) => new Date(`${d}T00:00:00`).toLocaleDateString("pt-BR");
const VAZIO = { automovelId: "", dataInicio: "", dataFim: "", valorMensal: "", solicitaCredito: false };

function PedidoForm({ automoveis, inicial, onSalvar, onCancelar }) {
  const [f, setF] = useState(inicial ?? VAZIO);
  const [erro, setErro] = useState("");
  const set = (k) => (e) => setF({ ...f, [k]: e.target.type === "checkbox" ? e.target.checked : e.target.value });
  const enviar = async (e) => {
    e.preventDefault(); setErro("");
    try { await onSalvar({ ...f, automovelId: Number(f.automovelId), valorMensal: Number(f.valorMensal) }); setF(VAZIO); }
    catch (err) { setErro(err.message); }
  };
  return (
    <form className="form grid5" onSubmit={enviar}>
      <label className="span2">Automóvel<select value={f.automovelId} onChange={set("automovelId")} required>
        <option value="">Selecione…</option>
        {automoveis.map((a) => <option key={a.id} value={a.id}>{a.marca} {a.modelo} {a.ano} · {a.placa}</option>)}
      </select></label>
      <label>Início<input type="date" value={f.dataInicio} onChange={set("dataInicio")} required /></label>
      <label>Fim<input type="date" value={f.dataFim} onChange={set("dataFim")} required /></label>
      <label>Valor mensal (R$)<input type="number" min="0.01" step="0.01" value={f.valorMensal} onChange={set("valorMensal")} required /></label>
      <label className="check"><input type="checkbox" checked={f.solicitaCredito} onChange={set("solicitaCredito")} /> Solicitar contrato de crédito bancário</label>
      {erro && <p className="error span5">{erro}</p>}
      <div className="actions">
        <button className="btn">Salvar pedido</button>
        {onCancelar && <button type="button" className="btn ghost" onClick={onCancelar}>Cancelar</button>}
      </div>
    </form>
  );
}

function AcoesAgente({ pedido, tipoAgente, onAtualizado }) {
  const [modo, setModo] = useState(null);
  const [parecer, setParecer] = useState("");
  const [c, setC] = useState({ tipoProprietario: tipoAgente, valorCredito: "", taxaJurosMensal: "", parcelas: "" });
  const [erro, setErro] = useState("");
  const exec = async (fn) => { setErro(""); try { onAtualizado(await fn()); setModo(null); } catch (e) { setErro(e.message); } };

  if (pedido.status === "PENDENTE") {
    if (modo !== "avaliar") return <button className="btn ghost" onClick={() => setModo("avaliar")}>Avaliar</button>;
    return (
      <div className="inline-form">
        <textarea placeholder="Parecer financeiro" value={parecer} onChange={(e) => setParecer(e.target.value)} rows={2} />
        <div className="actions">
          <button className="btn" disabled={!parecer.trim()} onClick={() => exec(() => api.avaliarPedido(pedido.id, { aprovado: true, parecer }))}>Aprovar</button>
          <button className="btn danger" disabled={!parecer.trim()} onClick={() => exec(() => api.avaliarPedido(pedido.id, { aprovado: false, parecer }))}>Reprovar</button>
          <button className="btn ghost" onClick={() => setModo(null)}>Fechar</button>
        </div>
        {erro && <p className="error">{erro}</p>}
      </div>
    );
  }
  if (pedido.status === "APROVADO") {
    if (pedido.solicitaCredito && tipoAgente !== "BANCO") return <span className="muted">Aguardando banco</span>;
    if (modo !== "contrato") return <button className="btn" onClick={() => setModo("contrato")}>Executar contrato</button>;
    const set = (k) => (e) => setC({ ...c, [k]: e.target.value });
    const enviar = () => exec(() => api.contratarPedido(pedido.id, {
      tipoProprietario: c.tipoProprietario,
      ...(pedido.solicitaCredito ? { valorCredito: Number(c.valorCredito), taxaJurosMensal: Number(c.taxaJurosMensal), parcelas: Number(c.parcelas) } : {}),
    }));
    return (
      <div className="inline-form">
        <label>Proprietário do automóvel<select value={c.tipoProprietario} onChange={set("tipoProprietario")}>
          <option value="CLIENTE">Cliente</option><option value={tipoAgente}>{tipoAgente === "BANCO" ? "Banco" : "Empresa"}</option>
        </select></label>
        {pedido.solicitaCredito && (
          <div className="row">
            <input type="number" placeholder="Valor do crédito" value={c.valorCredito} onChange={set("valorCredito")} />
            <input type="number" step="0.01" placeholder="Juros % a.m." value={c.taxaJurosMensal} onChange={set("taxaJurosMensal")} />
            <input type="number" placeholder="Parcelas" value={c.parcelas} onChange={set("parcelas")} />
          </div>
        )}
        <div className="actions"><button className="btn" onClick={enviar}>Confirmar</button><button className="btn ghost" onClick={() => setModo(null)}>Fechar</button></div>
        {erro && <p className="error">{erro}</p>}
      </div>
    );
  }
  return null;
}

export default function Pedidos() {
  const { user } = useAuth();
  const cliente = user.perfil === "CLIENTE";
  const [pedidos, setPedidos] = useState([]);
  const [automoveis, setAutomoveis] = useState([]);
  const [editando, setEditando] = useState(null);
  const [filtro, setFiltro] = useState("");
  const [erro, setErro] = useState("");

  const carregar = () => api.listarPedidos().then(setPedidos).catch((e) => setErro(e.message));
  useEffect(() => { carregar(); api.listarAutomoveis().then(setAutomoveis).catch(() => {}); }, []);
  const substituir = (p) => setPedidos((l) => l.map((x) => (x.id === p.id ? p : x)));

  const cancelar = async (id) => {
    if (!confirm("Cancelar este pedido?")) return;
    try { substituir(await api.cancelarPedido(id)); } catch (e) { setErro(e.message); }
  };

  const visiveis = filtro ? pedidos.filter((p) => p.status === filtro) : pedidos;

  return (
    <>
      {cliente && (
        <section className="card">
          <h2>Novo pedido de aluguel</h2>
          <PedidoForm automoveis={automoveis} onSalvar={async (b) => { await api.criarPedido(b); carregar(); }} />
        </section>
      )}
      <section className="card">
        <div className="head">
          <h2>{cliente ? "Meus pedidos" : "Pedidos para análise"}</h2>
          <select value={filtro} onChange={(e) => setFiltro(e.target.value)}>
            <option value="">Todos os status</option>
            {["PENDENTE", "APROVADO", "REPROVADO", "CONTRATADO", "CANCELADO"].map((s) => <option key={s}>{s}</option>)}
          </select>
        </div>
        {erro && <p className="error">{erro}</p>}
        {!visiveis.length && <p className="muted">Nenhum pedido encontrado.</p>}
        <div className="list">
          {visiveis.map((p) => (
            <article key={p.id} className="pedido">
              <div className="pedido-head">
                <strong>#{p.id} · {p.automovel.marca} {p.automovel.modelo} ({p.automovel.placa})</strong>
                <StatusBadge status={p.status} />
              </div>
              <div className="meta">
                {!cliente && <span>Cliente: {p.clienteNome} · CPF {p.clienteCpf} · Renda {brl(p.rendaTotal)}</span>}
                <span>{data(p.dataInicio)} → {data(p.dataFim)}</span>
                <span>{brl(p.valorMensal)}/mês</span>
                {p.solicitaCredito && <span>Com crédito bancário</span>}
              </div>
              {p.parecer && <p className="parecer">Parecer de {p.agenteAvaliador}: {p.parecer}</p>}
              {p.contrato && (
                <p className="parecer">Contrato #{p.contrato.id} por {p.contrato.agente} · proprietário: {p.contrato.tipoProprietario}
                  {p.contrato.banco && ` · crédito ${brl(p.contrato.valorCredito)} em ${p.contrato.parcelas}x a ${p.contrato.taxaJurosMensal}% a.m. (${p.contrato.banco})`}</p>
              )}
              {editando === p.id ? (
                <PedidoForm automoveis={automoveis}
                  inicial={{ automovelId: p.automovel.id, dataInicio: p.dataInicio, dataFim: p.dataFim, valorMensal: p.valorMensal, solicitaCredito: p.solicitaCredito }}
                  onSalvar={async (b) => { substituir(await api.atualizarPedido(p.id, b)); setEditando(null); }}
                  onCancelar={() => setEditando(null)} />
              ) : (
                <div className="actions">
                  {((cliente && p.status === "PENDENTE") || (!cliente && ["PENDENTE", "APROVADO", "REPROVADO"].includes(p.status))) &&
                    <button className="btn ghost" onClick={() => setEditando(p.id)}>Modificar</button>}
                  {cliente && ["PENDENTE", "APROVADO"].includes(p.status) && <button className="btn ghost danger" onClick={() => cancelar(p.id)}>Cancelar</button>}
                  {!cliente && <AcoesAgente pedido={p} tipoAgente={user.tipoAgente} onAtualizado={substituir} />}
                </div>
              )}
            </article>
          ))}
        </div>
      </section>
    </>
  );
}
