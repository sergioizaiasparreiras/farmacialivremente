import React from 'react';
import { useNavigate, Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import logo from '../../assets/Logo.png'; 
import '../../styles/LayoutAdministrador.css';

const LayoutAdministrador: React.FC = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    // Remova o token de autenticação, se houver
    localStorage.removeItem('token'); // ou o nome do seu token
    sessionStorage.removeItem('token');
    // Qualquer outro cleanup necessário

    // Redireciona para a landing page
    navigate('/', { replace: true });
  };

  return (
    <div className="admin-layout">
      <header className="admin-header">
        <div className="header-left" onClick={() => navigate('/admin')} style={{ cursor: 'pointer' }}>
          <img src={logo} alt="Logo" className="admin-logo" />
          <div className="header-title">
            <span className="empresa">Farmácia Livremente</span>
            <span className="sistema">Painel Administrativo</span>
          </div>
        </div>

        <div className="usuario-logado">
          Bem-vindo, Admin
          <button className="btn-sair" onClick={handleLogout} style={{ marginLeft: 16 }}>
            Sair
          </button>
        </div>
      </header>

      <div className="admin-body">
        <Sidebar />
        <main className="admin-main">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default LayoutAdministrador;