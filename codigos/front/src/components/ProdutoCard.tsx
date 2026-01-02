import React from 'react';
import fallbackImg from '../assets/Logo.png';
import '../styles/ProdutosContainer.css';

type Produto = {
  id: number;
  name: string;
  photo: string;
  price: number;
  available: boolean;
  description: string;
  type: 'REVENDA' | 'HOMEOPATICO';
  categories: string[];
};

interface ProdutoCardProps {
  produto: Produto;
  onAdicionarAoCarrinho: (productId: number) => void;
  onVerDetalhes: (produto: Produto) => void;
}

const ProdutoCard: React.FC<ProdutoCardProps> = ({
  produto,
  onAdicionarAoCarrinho,
  onVerDetalhes,
}) => {
  const isBase64 = (str: string) => {
    try {
      return btoa(atob(str)) === str;
    } catch (err) {
      return false;
    }
  };

  const getImageSource = () => {
    if (isBase64(produto.photo)) {
      return `data:image/jpeg;base64,${produto.photo}`;
    }
    return produto.photo || fallbackImg;
  };

  const handleAdicionarClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onAdicionarAoCarrinho(produto.id);
  };

  const handleCardClick = () => {
    onVerDetalhes(produto);
  };

  return (
    <div className="produtoCard" onClick={handleCardClick}>
      <div className="link-imagem">
        <img
          src={getImageSource()}
          alt={produto.name}
          onError={(e) => {
            const target = e.target as HTMLImageElement;
            target.onerror = null;
            target.src = fallbackImg;
          }}
        />
      </div>

      <div className="link-nome">
        <h3>{produto.name}</h3>
      </div>

      <div className="descricao-tooltip-container">
        <p className="descricao">
          Descrição
          <span className="info-icon">i</span>
        </p>
        <span className="tooltip">{produto.description}</span>
      </div>

      <p>R$ {produto.price.toFixed(2)}</p>

      <button className="botaoCarrinho" onClick={handleAdicionarClick}>
        Adicionar ao carrinho
      </button>

      <span className={produto.available ? 'disponivel' : 'indisponivel'}>
        {produto.available ? 'Disponível' : 'Indisponível'}
      </span>
    </div>
  );
};

export default ProdutoCard;