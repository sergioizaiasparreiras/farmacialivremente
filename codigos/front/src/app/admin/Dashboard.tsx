import React from 'react';
import { Navigate } from 'react-router-dom';
import '../../styles/Dashboard.css';
import graficoUsuarios from '../../assets/grafico_usuarios_completo.png';
import graficoErros from '../../assets/grafico_erros_completo.png';

/**
 * Dashboard administrativo com visão geral do sistema
 * @component
 * @returns {JSX.Element} Elemento JSX contendo a dashboard
 */
const Dashboard: React.FC = (): JSX.Element => {
  const role = localStorage.getItem('userRole');
  const email = localStorage.getItem('userEmail');

  if (role !== 'ADMIN' && email !== 'admin@admin.com') {
    return <Navigate to="/login" replace />;
  }

  return (
    <div className="dashboard">
      <h2 className="dashboard-title">Visão Geral do Sistema</h2>

      <div className="charts-grid">
        <div className="chart-box">
          <h3 className="chart-title">Crescimento de Usuários</h3>
          <img src={graficoUsuarios} alt="Gráfico de crescimento de usuários" />
        </div>
        <div className="chart-box">
          <h3 className="chart-title">Distribuição de Tipos de Erros</h3>
          <img src={graficoErros} alt="Gráfico de tipos de erros" />
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
