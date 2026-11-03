import { Navigate, Route, Routes } from "react-router-dom";
import Layout from "./components/Layout.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import Login from "./pages/Login.jsx";
import Cadastro from "./pages/Cadastro.jsx";
import Pedidos from "./pages/Pedidos.jsx";
import Automoveis from "./pages/Automoveis.jsx";
import Perfil from "./pages/Perfil.jsx";
import Clientes from "./pages/Clientes.jsx";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/cadastro" element={<Cadastro />} />
      <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        <Route path="/pedidos" element={<Pedidos />} />
        <Route path="/automoveis" element={<Automoveis />} />
        <Route path="/perfil" element={<ProtectedRoute perfil="CLIENTE"><Perfil /></ProtectedRoute>} />
        <Route path="/clientes" element={<ProtectedRoute perfil="AGENTE"><Clientes /></ProtectedRoute>} />
      </Route>
      <Route path="*" element={<Navigate to="/pedidos" replace />} />
    </Routes>
  );
}
