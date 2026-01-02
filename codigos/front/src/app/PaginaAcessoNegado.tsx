// PaginaAcessoNegado.tsx
import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/PaginaAcessoNegado.css';

const PaginaAcessoNegado: React.FC = () => {
  return (
    <div className="pagina-acesso-negado">
      <div className="container-acesso-negado">
        <h1>Acesso Negado</h1>
        <p>Você não tem permissão para acessar esta página.</p>
        <div className="acoes-acesso-negado">
          <Link to="/landing" className="btn-primario">Voltar para a página inicial</Link>
          <Link to="/login" className="btn-secundario">Fazer login com outra conta</Link>
        </div>
      </div>
    </div>
  );
};

export default PaginaAcessoNegado;