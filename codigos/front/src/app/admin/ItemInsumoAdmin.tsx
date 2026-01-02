
import '../../styles/PaginaInsumosAdmin.css';

interface Insumo {
  id: number;
  name: string;
  available: boolean;
}

interface Props {
  insumo: Insumo;
  onAtualizarDisponibilidade: (id: number, novoStatus: boolean) => void;
}

const ItemInsumo = ({ insumo, onAtualizarDisponibilidade }: Props) => {
  return (
    <div className="item-insumo">
      <div className="info">
        <strong className="item-nome">{insumo.name}</strong>
      </div>
      <div className="botoes">
        <button
          className={insumo.available ? 'btn-status ativo' : 'btn-status'}
          onClick={() => onAtualizarDisponibilidade(insumo.id, true)}
        >
          SIM
        </button>
        <button
          className={!insumo.available ? 'btn-status ativo' : 'btn-status'}
          onClick={() => onAtualizarDisponibilidade(insumo.id, false)}
        >
          NÃO
        </button>
      </div>
    </div>
  );
};

export default ItemInsumo;