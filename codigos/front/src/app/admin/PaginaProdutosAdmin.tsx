import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaPlus, FaEdit, FaTrash } from 'react-icons/fa';
import '../../styles/PaginaProdutosAdmin.css';
import { getAllProdutos } from '../../services/productService';

interface Produto {
  id: number;
  nome: string;
  descricao: string;
  preco: number;
  disponivel: boolean;
  tipo: 'HOMEOPATICO' | 'REVENDA';
  categorias: string[];
  imagemUrl?: string;
}

const PaginaProdutosAdmin: React.FC = () => {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [termoBusca, setTermoBusca] = useState<string>('');
  const navigate = useNavigate();

 useEffect(() => {
  const carregarProdutos = async () => {
    try {
      const response = await getAllProdutos();

      const produtosCorrigidos = response.data.map((p: any) => ({
        ...p,
        preco: p.price, // mapeia 'price' → 'preco'
        descricao: p.description,
        nome: p.name,
        disponivel: p.available,
        tipo: p.type,
      }));

      setProdutos(produtosCorrigidos);
    } catch (error) {
      console.error('Erro ao carregar produtos:', error);
    } finally {
      setLoading(false);
    }
  };

  carregarProdutos();
}, []);

  const handleAddProduto = () => {
    navigate('/admin/produtos/incluir');
  };

  const handleEditarProduto = (id: number) => {
    navigate(`/admin/produtos/alterar/${id}`);
  };

  const handleExcluirProduto = (id: number) => {
    navigate(`/admin/produtos/excluir/${id}`);
  };

  const filtrarProdutos = () => {
    if (!termoBusca) {
      return produtos;
    }
    return produtos.filter((produto) => 
      produto.nome.toLowerCase().includes(termoBusca.toLowerCase()) ||
      produto.descricao.toLowerCase().includes(termoBusca.toLowerCase())
    );
  };

  return (
    <div className="produtos-container admin-theme-light">
      <div className="cabecalho-pagina">
        <h1>Lista dos Produtos</h1>
        <p className="subtitulo">Gerencie os produtos disponíveis no sistema</p>
      </div>
      
      {/* Campo de busca */}
      <div className="busca-admin-container">
        <input
          type="text"
          placeholder="Buscar produtos por nome ou descrição..."
          value={termoBusca}
          onChange={(e) => setTermoBusca(e.target.value)}
          className="campo-busca-admin"
        />
      </div>
      
      <div className="produtos-header">
        <button className="btn-adicionar" onClick={handleAddProduto} title="Adicionar Novo Produto">
          <FaPlus />
          Adicionar Novo Produto
        </button>
      </div>

      {loading ? (
        <div className="loading">Carregando produtos...</div>
      ) : (
        <div className="produtos-lista">
          {filtrarProdutos().map((produto) => (
            <div className="produto-item" key={produto.id}>
              <div className="produto-info">
                <div className="produto-nome">{produto.nome}</div>
                <div className="produto-descricao">{produto.descricao}</div>
                <div className="produto-detalhes">
                  <span className={`produto-status ${produto.disponivel ? 'disponivel' : 'indisponivel'}`}>
                    {produto.disponivel ? 'Disponível' : 'Indisponível'}
                  </span>
                  <span className="produto-tipo">{produto.tipo}</span>
                  <span className="produto-preco">
                    {produto.preco !== undefined && produto.preco !== null && !isNaN(produto.preco)
                      ? `R$ ${produto.preco.toFixed(2)}`
                      : 'Preço não disponível'}
                  </span>
                </div>
              </div>
              <div className="produto-acoes">
                <button className="btn-editar" onClick={() => handleEditarProduto(produto.id)} title="Editar produto">
                  <FaEdit />
                </button>
                <button className="btn-excluir" onClick={() => handleExcluirProduto(produto.id)} title="Excluir produto">
                  <FaTrash />
                </button>
              </div>
            </div>
          ))}
          
          {filtrarProdutos().length === 0 && !loading && (
            <div className="sem-resultados">
              {termoBusca ? 
                `Nenhum produto encontrado para "${termoBusca}".` : 
                'Nenhum produto cadastrado.'
              }
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default PaginaProdutosAdmin;
