import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

export default function ProtectedRoute({ children, perfil }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="center muted">Carregando…</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (perfil && user.perfil !== perfil) return <Navigate to="/pedidos" replace />;
  return children;
}
