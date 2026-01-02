import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../styles/PaginaAdicionarBairro.css';
import { api } from '../../services/api';

const PaginaAdicionarBairro = () => {
  const [nome, setNome] = useState('');
  const [taxa, setTaxa] = useState('');
  const [erro, setErro] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!nome.trim()) {
      setErro('O nome do bairro é obrigatório.');
      return;
    }
    
    const taxaNumerica = parseFloat(taxa);
    if (isNaN(taxaNumerica) || taxaNumerica < 0) {
      setErro('A taxa de entrega deve ser um valor numérico válido e positivo.');
      return;
    }

    try {
      setEnviando(true);
      setErro(null);
      
      await api.post('/neighborhoods', {
        name: nome.trim(),  // Corrigido para 'name' (esperado pelo backend)
        tax: parseFloat(taxaNumerica.toFixed(2)) // Corrigido para 'tax' e formatado
      });
      
      navigate('/admin/bairros');
    } catch (error: any) {
      console.error('Erro ao adicionar bairro:', error);
      setErro(error.response?.data?.message || 'Não foi possível adicionar o bairro. Tente novamente.');
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div className="pagina-adicionar-bairro">
      <h1>Farmácia Homeopática Livremente - Adicionar novo bairro</h1>
      
      <div className="formulario-container">
        <form onSubmit={handleSubmit}>
          <div className="campo-formulario">
            <label htmlFor="nome-bairro">Nome do bairro</label>
            <input
              type="text"
              id="nome-bairro"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              placeholder="Digite o nome do bairro"
              required
            />
          </div>
          
          <div className="campo-formulario">
            <label htmlFor="taxa-entrega">Taxa (R$)</label>
            <input
              type="number"
              id="taxa-entrega"
              value={taxa}
              onChange={(e) => setTaxa(e.target.value)}
              placeholder="0.00"
              step="0.01"
              min="0"
              required
            />
          </div>
          
          {erro && <div className="mensagem-erro">{erro}</div>}
          
          <div className="acoes-formulario">
            <button 
              type="button" 
              className="btn-cancelar"
              onClick={() => navigate('/admin/bairros')}
            >
              Cancelar
            </button>
            <button 
              type="submit" 
              className="btn-salvar"
              disabled={enviando}
            >
              {enviando ? 'Salvando...' : 'Salvar bairro'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default PaginaAdicionarBairro;