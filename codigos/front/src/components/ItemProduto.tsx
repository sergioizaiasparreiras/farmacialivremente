import { Link } from "react-router-dom";
import '../styles/ItemProduto.css';

interface Props {
  id: number;
  nome: string;
  preco: number;
  quantidade: number;
  photo: string;
  type: 'REVENDA' | 'HOMEOPATICO';
  onQuantidadeChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onRemover: () => void;
}

const fallbackImg = "https://via.placeholder.com/150";

const ItemProduto: React.FC<Props> = ({ id, nome, photo, preco, type, quantidade, onQuantidadeChange, onRemover }) => {
  return (
    <div className="item-produto">
      <div className="info">
        <div className="img-produto">
          <img
            src={photo}
            alt={nome}
            onError={(e) => {
              const target = e.target as HTMLImageElement;
              target.onerror = null;
              target.src = fallbackImg;
            }}
          />
        </div>
        <div className="info-texto">
          <p className="nome-produto">{nome}</p>
          <p className="id-produto">Código: {id}</p>
          <span className="tipo-produto-tag">{type === 'REVENDA' ? 'Revenda' : 'Homeopático'}</span>
          <p className="link-produto"><Link to={`/detalhes/produto/${id}`} className="link-ver">Ver Produto</Link></p>  
        </div>

        <div className="acoes">
          <input
            type="number"
            min="1"
            value={quantidade}
            onChange={onQuantidadeChange}
            className="quantidade-input"
          />
          <div className="remover">
            <button onClick={onRemover} className="botao-remover">Remover</button>
          </div>
        </div>
        <p className="preco-produto">R${preco.toFixed(2)}</p>
      </div>
    </div>
  );
};

export default ItemProduto;