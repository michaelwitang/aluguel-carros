import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { api, tokenStore } from "../api/client.js";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(Boolean(tokenStore.get()));

  useEffect(() => {
    if (!tokenStore.get()) return;
    api.me().then(setUser).catch(() => tokenStore.clear()).finally(() => setLoading(false));
  }, []);

  const entrar = useCallback((resp) => { tokenStore.set(resp.token); setUser(resp); }, []);
  const login = useCallback(async (username, senha) => entrar(await api.login({ username, senha })), [entrar]);
  const logout = useCallback(() => { tokenStore.clear(); setUser(null); }, []);

  return <AuthContext.Provider value={{ user, loading, login, logout, entrar }}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
