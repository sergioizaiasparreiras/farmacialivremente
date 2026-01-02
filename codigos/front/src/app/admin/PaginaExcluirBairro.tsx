// PaginaExcluirBairro.tsx
import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import '../../styles/PaginaExcluirBairro.css';
import { api } from '../../services/api';

interface Bairro {
  id: number;
  name: string;
  tax: number;
}

const PaginaExcluirBairro = () => {
  const { id } = useParams<{ id: string }>();
  const [bairro, setBairro] = useState<Bairro | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [excluindo, setExcluindo] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const carregarBairro = async () => {
      try {
        setCarregando(true);
        setErro(null);
        
        // Simulação para desenvolvimento
        const bairroSimulado: Bairro = {
          id: parseInt(id || '0'),
          name: 'Nome do Bairro',
          tax: 8.50
        };
        
        try {
          //Colocar aqui o endpoint correto:
          const resposta = await api.get(`/neighborhoods/${id}`);
          setBairro(resposta.data || bairroSimulado);
        } catch (error) {
          console.error('Erro ao carregar bairro da API:', error);
          // Usar dados simulados em caso de erro
          setBairro(bairroSimulado);
        }
      } catch (error) {
        console.error('Erro ao carregar bairro:', error);
        setErro('Não foi possível carregar os dados do bairro.');
      } finally {
        setCarregando(false);
      }
    };
    
    carregarBairro();
  }, [id]);

  const handleExcluir = async () => {
    try {
      setExcluindo(true);
      setErro(null);
      
      await api.delete(`/neighborhoods/${id}`);
      
      navigate('/admin/bairros');
    } catch (error) {
      console.error('Erro ao excluir bairro:', error);
      setErro('Não foi possível excluir o bairro. Tente novamente.');
      setExcluindo(false);
    }
  };

  if (carregando) {
    return (
      <div className="pagina-excluir-bairro">
        <div className="carregando">Carregando informações do bairro...</div>
      </div>
    );
  }

  if (!bairro) {
    return (
      <div className="pagina-excluir-bairro">
        <div className="mensagem-erro">Bairro não encontrado.</div>
        <button 
          className="btn-voltar"
          onClick={() => navigate('/admin/bairros')}
        >
          Voltar para lista de bairros
        </button>
      </div>
    );
  }

  return (
    <div className="pagina-excluir-bairro">
      <h1>Exclusão do bairro</h1>
      
      <div className="confirmacao-exclusao">
        <div className="info-bairro">
          <p className="nome-bairro">Nome do bairro: {bairro.name}</p>
          <p className="taxa-bairro">Taxa: R$ {bairro.tax.toFixed(2)}</p>
        </div>
        
        <div className="aviso-exclusao">
          <p>Deseja realmente excluir este bairro?</p>
          <p className="aviso-irreversivel">Esta ação é irreversível!</p>
        </div>
        
        {erro && <div className="mensagem-erro">{erro}</div>}
        
        <div className="acoes-exclusao">
          <button 
            className="btn-cancelar"
            onClick={() => navigate('/admin/bairros')}
            disabled={excluindo}
          >
            Cancelar
          </button>
          <button 
            className="btn-excluir"
            onClick={handleExcluir}
            disabled={excluindo}
          >
            {excluindo ? 'Excluindo...' : 'Sim, excluir'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default PaginaExcluirBairro;