import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import '../../styles/Sidebar.css';

const Sidebar: React.FC = () => {
  const { pathname } = useLocation();

  const isActive = (path: string) => pathname.startsWith(path);

  return (
    <aside className="sidebar">
      <div className="sidebar-title">Farmácia Livremente</div>
      <nav className="sidebar-nav">

        <Link to="/admin/produtos" className={isActive('/admin/produtos') ? 'active' : ''}>
          Produtos
        </Link>
        <Link to="/admin/pedidos" className={isActive('/admin/pedidos') ? 'active' : ''}>
          Pedidos
        </Link>
        <Link to="/admin/insumos" className={isActive('/admin/insumos') ? 'active' : ''}>
          Insumos
        </Link>
         <Link to="/admin/bairros" className={isActive('/admin/bairros') ? 'active' : ''}>
          Bairros e Taxas
        </Link>
      </nav>
    </aside>
  );
};

export default Sidebar;
