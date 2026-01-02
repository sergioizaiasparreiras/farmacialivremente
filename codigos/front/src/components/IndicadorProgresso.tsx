
import '../styles/IndicadorProgresso.css';

interface Props {
  passos: number;
  passoAtual: number;
}

const IndicadorProgresso: React.FC<Props> = ({ passos, passoAtual }) => {
  return (
    <div className="indicador-progresso">
      {Array.from({ length: passos }).map((_, index) => (
        <div key={index} className="passo-container">
        <div
          className={`bolinha ${index + 1 <= passoAtual ? 'ativo' : ''}`}
        >
          {index + 1}
        </div>
        </div>
      ))}
    </div>
  );
};

export default IndicadorProgresso;
