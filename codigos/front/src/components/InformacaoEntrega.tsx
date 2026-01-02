import '../styles/InformacaoEntrega.css';
import { useState, useRef, useEffect } from 'react';

interface Props {
  total: number;
  frete: number;
  onFinalizar: () => void;
  bairroSelecionado: string;
  observacao: string;
  bairros: string[];
  onAlterarBairro: (bairro: string) => void;
  onAlterarObservacao?: (obs: string) => void;
  onContinuarCompra: () => void;
}

const InformacaoEntrega: React.FC<Props> = ({
  total,
  frete,
  onFinalizar,
  bairroSelecionado,
  observacao,
  bairros,
  onAlterarBairro,
  onAlterarObservacao,
  onContinuarCompra
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const selectRef = useRef<HTMLDivElement>(null);

  const filteredBairros = bairros.filter(bairro =>
    bairro.toLowerCase().includes(searchTerm.toLowerCase())
  );

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (selectRef.current && !selectRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <div className="informacao-entrega">
      <div className="custom-select-container" ref={selectRef}>
        <div 
          className="custom-select"
          onClick={() => setIsOpen(!isOpen)}
        >
          {bairroSelecionado || 'Selecione um bairro'}
        </div>
        
        {isOpen && (
          <div className="custom-select-dropdown">
            <input
              type="text"
              placeholder="Pesquisar bairro..."
              className="custom-select-search"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              autoFocus
            />
            
            <div className="custom-select-options">
              {filteredBairros.length > 0 ? (
                filteredBairros.map((bairro) => (
                  <div
                    key={bairro}
                    className={`custom-select-option ${
                      bairro === bairroSelecionado ? 'selected' : ''
                    }`}
                    onClick={() => {
                      onAlterarBairro(bairro);
                      setIsOpen(false);
                      setSearchTerm('');
                    }}
                  >
                    {bairro}
                  </div>
                ))
              ) : (
                <div className="custom-select-no-results">Nenhum bairro encontrado</div>
              )}
            </div>
          </div>
        )}
      </div>

      <div className="form">
        <label className="label">Adicione alguma observação</label>
        <textarea
          className="textoarea"
          rows={3}
          value={observacao}
          onChange={(e) => onAlterarObservacao?.(e.target.value)}
        ></textarea>
      </div>

      <div className="resumo-precos">
        <p className="frete">Frete: R$ {frete.toFixed(2)}</p>
        <p className="total">Total: R$ {total.toFixed(2)}</p>
      </div>

      <div className="botoes-acao">
        <button onClick={onContinuarCompra} className="botao-continuar-compra">
          Continuar Comprando
        </button>
        <button onClick={onFinalizar} className="botao-finalizar">
          Finalizar Compra
        </button>
      </div>
    </div>
  );
};

export default InformacaoEntrega;