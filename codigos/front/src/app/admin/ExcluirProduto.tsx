import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../../styles/ExcluirProduto.css';
import { getProdutoById, deleteProduto } from '../../services/productService';

interface Produto {
  id: number;
  name: string;
  description: string;
  price: number;
  available: boolean;
  type: string;
  categories: string[];
  photo?: string; // base64
}

export default function ExcluirProduto() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [produto, setProduto] = useState<Produto | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    console.log('ID recebido via useParams:', id);
    const carregarProduto = async () => {
      try {
        if (!id) return;
        const response = await getProdutoById(Number(id));
        console.log('Produto recebido do backend:', response.data);
        setProduto(response.data);
      } catch (error) {
        setErro('Produto não encontrado ou já foi excluído.');
        setProduto(null);
      } finally {
        setLoading(false);
      }
    };
    carregarProduto();
  }, [id]);

  const handleConfirmarExclusao = async () => {
    console.log('Tentando excluir produto com id:', id);
    if (!id) {
      alert('ID do produto não encontrado!');
      return;
    }
    try {
      await deleteProduto(Number(id));
      alert('Produto excluído com sucesso!');
      navigate('/admin/produtos');
    } catch (error: any) {
      let msg = 'Erro ao excluir produto.';
      if (error.response) {
        msg += `\nStatus: ${error.response.status}`;
        msg += `\nMensagem: ${error.response.data?.message || error.response.data}`;
        console.error('Erro detalhado:', error.response);
      } else if (error.request) {
        msg += '\nSem resposta do servidor.';
        console.error('Sem resposta:', error.request);
      } else {
        msg += `\n${error.message}`;
        console.error('Erro desconhecido:', error);
      }
      alert(msg);
    }
  };

  const handleCancelar = () => {
    navigate('/admin/produtos');
  };

  if (loading) {
    return <div className="loading">Carregando informações do produto...</div>;
  }

  if (erro) {
    return <div className="error">{erro}</div>;
  }

  if (!produto) {
    return <div className="error">Produto não encontrado.</div>;
  }

  return (
    <div className="excluir-produto-container">
      <h2 className="excluir-produto-title">Exclusão de Produto</h2>

      <div className="excluir-produto-card">
        <div className="produto-info">
          <h3>{produto.name || '---'}</h3>
          <p className="produto-descricao">{produto.description || '---'}</p>
          <p className="produto-preco">
            R$ {typeof produto.price === 'number' ? produto.price.toFixed(2) : '---'}
          </p>
          <p className="produto-tipo">Tipo: {produto.type || '---'}</p>
          <p className="produto-disponibilidade">
            Status: {produto.available ? 'Disponível' : 'Indisponível'}
          </p>
          <div className="produto-categorias">
            Categorias:
            {produto.categories && produto.categories.length > 0
              ? produto.categories.map((cat, index) => (
                  <span key={index} className="categoria-tag">
                    {cat}
                  </span>
                ))
              : '---'}
          </div>
          {/* Exibe a imagem do produto, se existir */}
          {produto.photo && (
            <img
              src={`data:image/png;base64,${produto.photo}`}
              alt="Imagem do produto"
              className="preview-imagem"
              style={{ marginTop: 16, maxWidth: 200, maxHeight: 120 }}
              onError={e => (e.currentTarget.style.display = 'none')}
            />
          )}
        </div>
      </div>

      <div className="excluir-produto-warning">
        <p>Deseja realmente excluir esse produto?</p>
        <p className="warning-text">Essa ação é irreversível</p>
      </div>

      <div className="excluir-produto-actions">
        <button type="button" className="btn-confirmar" onClick={handleConfirmarExclusao}>
          Confirmar Exclusão
        </button>
        <button type="button" className="btn-cancelar" onClick={handleCancelar}>
          Cancelar
        </button>
      </div>
    </div>
  );
}