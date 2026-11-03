const BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080/api";
const TOKEN_KEY = "aluguel.token";

export const tokenStore = {
  get: () => localStorage.getItem(TOKEN_KEY),
  set: (t) => localStorage.setItem(TOKEN_KEY, t),
  clear: () => localStorage.removeItem(TOKEN_KEY),
};

async function request(path, { method = "GET", body } = {}) {
  const headers = { "Content-Type": "application/json" };
  const token = tokenStore.get();
  if (token) headers.Authorization = `Bearer ${token}`;
  const res = await fetch(`${BASE}${path}`, { method, headers, body: body ? JSON.stringify(body) : undefined });
  if (res.status === 401 && token) {
    tokenStore.clear();
    window.location.assign("/login");
  }
  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data.message || `Erro ${res.status}`);
  }
  return res.status === 204 ? null : res.json();
}

export const api = {
  login: (b) => request("/auth/login", { method: "POST", body: b }),
  me: () => request("/auth/me"),
  registrarAgente: (b) => request("/auth/registrar/agente", { method: "POST", body: b }),

  criarCliente: (b) => request("/clientes", { method: "POST", body: b }),
  listarClientes: () => request("/clientes"),
  buscarCliente: (id) => request(`/clientes/${id}`),
  atualizarCliente: (id, b) => request(`/clientes/${id}`, { method: "PUT", body: b }),
  excluirCliente: (id) => request(`/clientes/${id}`, { method: "DELETE" }),

  listarAutomoveis: () => request("/automoveis"),
  criarAutomovel: (b) => request("/automoveis", { method: "POST", body: b }),
  atualizarAutomovel: (id, b) => request(`/automoveis/${id}`, { method: "PUT", body: b }),
  excluirAutomovel: (id) => request(`/automoveis/${id}`, { method: "DELETE" }),

  listarPedidos: () => request("/pedidos"),
  criarPedido: (b) => request("/pedidos", { method: "POST", body: b }),
  atualizarPedido: (id, b) => request(`/pedidos/${id}`, { method: "PUT", body: b }),
  cancelarPedido: (id) => request(`/pedidos/${id}/cancelar`, { method: "PATCH" }),
  avaliarPedido: (id, b) => request(`/pedidos/${id}/avaliar`, { method: "PATCH", body: b }),
  contratarPedido: (id, b) => request(`/pedidos/${id}/contrato`, { method: "POST", body: b }),
};
