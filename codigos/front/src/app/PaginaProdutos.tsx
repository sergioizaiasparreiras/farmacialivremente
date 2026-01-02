import React, { useState, useEffect } from 'react';
import '../styles/PaginaProdutos.css';
import '../styles/ProdutosContainer.css';
import ListaDeProdutos from '../components/ListaDeProdutos';
import ProdutoCard from '../components/ProdutoCard';
import fallbackImg from '../assets/Logo.png';
import { api } from '../services/api';

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

const categoriasDisponiveis = ['CREMES', 'ARNICA', 'GEIS', 'POMADAS'];

const PaginaProdutos: React.FC = () => {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [filtroTipo, setFiltroTipo] = useState<'REVENDA' | 'HOMEOPATICO' | null>(null);
  const [filtroCategoria, setFiltroCategoria] = useState<string | null>(null);
  const [termoBusca, setTermoBusca] = useState<string>('');
  const [produtoSelecionado, setProdutoSelecionado] = useState<Produto | null>(null);
  const [mensagemFeedback, setMensagemFeedback] = useState<{
    tipo: 'sucesso' | 'erro';
    texto: string;
  } | null>(null);

  const isBase64 = (str: string | null | undefined): boolean => {
    if (!str) return false;
    try {
      return btoa(atob(str)) === str;
    } catch (err) {
      return false;
    }
  };

  const adicionarAoCarrinho = async (productId: number) => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        setMensagemFeedback({
          tipo: 'erro',
          texto: 'Você precisa estar logado para adicionar produtos ao carrinho.'
        });
        setTimeout(() => setMensagemFeedback(null), 4000);
        return;
      }
      
      const response = await api.post(`/cart/add?productId=${productId}`);

      console.log('Produto adicionado com sucesso:', response.data);
      
      window.dispatchEvent(new CustomEvent('cartUpdated'));
      
      setMensagemFeedback({
        tipo: 'sucesso',
        texto: 'Produto adicionado ao carrinho com sucesso!'
      });
      setTimeout(() => setMensagemFeedback(null), 3000);
      
    } catch (error: any) {
      console.error('Erro ao adicionar ao carrinho:', error);
      let mensagemErro = 'Não foi possível adicionar o produto ao carrinho.';

      // Lógica para tratar erros específicos da resposta da API
      if (error.response && error.response.status === 400) {
        const errorMsg = error.response.data?.message || error.response.data;
        if (typeof errorMsg === 'string' && (errorMsg.includes('tipo') || errorMsg.includes('homeopático') || errorMsg.includes('revenda'))) {
          mensagemErro = 'Não é possível misturar produtos homeopáticos com medicamentos de revenda no mesmo carrinho.';
        } else {
            mensagemErro = errorMsg || 'Ocorreu um erro ao processar o seu pedido.';
        }
      }
      
      setMensagemFeedback({
        tipo: 'erro',
        texto: mensagemErro
      });
      setTimeout(() => setMensagemFeedback(null), 5000);
    }
  };
  
  /**
   * Carrega a lista de todos os produtos do backend.
   */
  const carregarProdutos = () => {
    // Usa a instância 'api' do Axios para fazer a requisição.
    api.get('/product')
      .then((response) => {
        setProdutos(response.data);
      })
      .catch((error) => {
        console.error('Erro ao buscar produtos:', error);
        setMensagemFeedback({
            tipo: 'erro',
            texto: 'Não foi possível carregar os produtos. Tente novamente mais tarde.'
        });
      });
  };

  useEffect(() => {
    carregarProdutos();
  }, []);

  const filtrarProdutos = () => {
    return produtos.filter((produto) => {
      const tipoMatch = filtroTipo ? produto.type === filtroTipo : true;
      const categoriaMatch = filtroCategoria ? produto.categories.includes(filtroCategoria) : true;
      const buscaMatch = termoBusca ? 
        produto.name.toLowerCase().includes(termoBusca.toLowerCase()) ||
        produto.description.toLowerCase().includes(termoBusca.toLowerCase())
        : true;
      return tipoMatch && categoriaMatch && buscaMatch;
    });
  };

  const limparFiltros = () => {
    setFiltroTipo(null);
    setFiltroCategoria(null);
    setTermoBusca('');
  };

  return (
    <>
      {/* Mensagem de feedback */}
      {mensagemFeedback && (
        <div className={`mensagem-feedback ${mensagemFeedback.tipo}`}>
          {mensagemFeedback.texto}
        </div>
      )}
      
      <div className="pagina-produtos-container">
        {/* Campo de busca */}
        <div className="busca-container">
          <input
            type="text"
            placeholder="Buscar produtos por nome ou descrição..."
            value={termoBusca}
            onChange={(e) => setTermoBusca(e.target.value)}
            className="campo-busca"
          />
        </div>
        
        <div className="categoriasProd">
          <div className="sectionCategoriasProd">
            <ul>
              <li
                className={filtroTipo === 'REVENDA' ? 'ativo' : ''}
                onClick={() => setFiltroTipo((prev) => (prev === 'REVENDA' ? null : 'REVENDA'))}
              >
                Medicamentos de Revenda
              </li>
              <li
                className={filtroTipo === 'HOMEOPATICO' ? 'ativo' : ''}
                onClick={() =>
                  setFiltroTipo((prev) => (prev === 'HOMEOPATICO' ? null : 'HOMEOPATICO'))
                }
              >
                Homeopáticos
              </li>
              <li onClick={limparFiltros} className="limpar-filtros">
                Limpar Filtros
              </li>
            </ul>
          </div>
        </div>

        <div className="principalProdutos">
          <div className="lista_Produtos">
            <ListaDeProdutos
              produtos={categoriasDisponiveis}
              onSelecionar={(categoria) =>
                setFiltroCategoria((prev) => (prev === categoria ? null : categoria))
              }
              filtroAtual={filtroCategoria}
            />
          </div>

          <div className="produtosContainer">
            {filtrarProdutos().length === 0 ? (
              <p className="mensagem-vazio">
                {termoBusca ? 
                  `Nenhum produto encontrado para "${termoBusca}".` : 
                  'Nenhum produto disponível no momento.'
                }
              </p>
            ) : (
              filtrarProdutos().map((produto) => (
                <ProdutoCard
                  key={produto.id}
                  produto={{
                    ...produto,
                    photo: produto.photo || `/api/product/${produto.id}/image`,
                  }}
                  onAdicionarAoCarrinho={adicionarAoCarrinho}
                  onVerDetalhes={setProdutoSelecionado}
                />
              ))
            )}
          </div>
        </div>
      </div>

      {produtoSelecionado && (
        <div className="modal-detalhes-overlay" onClick={() => setProdutoSelecionado(null)}>
          <div className="modal-detalhes-content" onClick={(e) => e.stopPropagation()}>
            <button className="modal-detalhes-close-btn" onClick={() => setProdutoSelecionado(null)}>
              &times;
            </button>
            <div className="modal-detalhes-grid">
              <img
                src={isBase64(produtoSelecionado.photo) ? `data:image/jpeg;base64,${produtoSelecionado.photo}` : (produtoSelecionado.photo || fallbackImg)}
                alt={produtoSelecionado.name}
                className="modal-detalhes-img"
                onError={(e) => {
                  const target = e.target as HTMLImageElement;
                  target.onerror = null;
                  target.src = fallbackImg;
                }}
              />
              <div className="modal-detalhes-info">
                <h1>{produtoSelecionado.name}</h1>
                <p className="modal-detalhes-descricao">{produtoSelecionado.description}</p>
                <div className="modal-detalhes-compra">
                  <p className="modal-detalhes-preco">R$ {produtoSelecionado.price.toFixed(2)}</p>
                  <span className={produtoSelecionado.available ? 'disponivel-tag' : 'indisponivel-tag'}>
                    {produtoSelecionado.available ? 'Disponível' : 'Indisponível'}
                  </span>
                </div>
                <button
                  className="modal-detalhes-add-btn"
                  disabled={!produtoSelecionado.available}
                  onClick={() => adicionarAoCarrinho(produtoSelecionado.id)}
                >
                  Adicionar ao carrinho
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default PaginaProdutos;