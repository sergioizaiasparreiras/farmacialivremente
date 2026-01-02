import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {api} from '../services/api';
import '../styles/MeusPedidos.css';
import logo from '../assets/Logo.png';

interface PedidoItem {
  productId: number; // Adicionado para usar no carrinho
  productName: string;
  quantity: number;
  price: number;
  productImageBase64?: string;
}

interface Pedido {
  id: number;
  orderDate: string;
  status: string;
  items: PedidoItem[];
  totalValue: number;
  deliveryTax: number;
  totalWithDelivery: number;
  fullName: string;
  neighborhoodName: string;
}

const MeusPedidos: React.FC = () => {
  const [filtro, setFiltro] = useState('Todos');
  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [modalProdutos, setModalProdutos] = useState({ aberto: false, pedido: null as Pedido | null });
  const [modalDetalhes, setModalDetalhes] = useState({ aberto: false, pedido: null as Pedido | null });
  const [atualizando, setAtualizando] = useState(false);
  const [statusOptions, setStatusOptions] = useState<{ name: string, descricao: string }[]>([]);
  
  // Estados para o "comprar novamente"
  const [isProcessing, setIsProcessing] = useState(false);
  const [mensagem, setMensagem] = useState('');
  const [mensagemTipo, setMensagemTipo] = useState<'sucesso' | 'erro' | ''>('');
  
  const navigate = useNavigate();

  useEffect(() => {
    const fetchStatusOptions = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;

        const { data } = await api.get('/orders/status-list', {
          headers: { Authorization: `Bearer ${token}` },
        });
        setStatusOptions(data);
      } catch (error) {
        console.error('Erro ao buscar status:', error);
      }
    };
    fetchStatusOptions();
  }, []);

  const fetchPedidos = async (mostrarCarregando = true) => {
    try {
      if (mostrarCarregando) setCarregando(true);
      else setAtualizando(true);

      const token = localStorage.getItem('token');
      if (!token) return navigate('/login');

      const { data } = await api.get('/orders/myOrders', {
        headers: { Authorization: `Bearer ${token}` },
      });

      setPedidos(data);
    } catch (error) {
      console.error('Erro ao buscar pedidos:', error);
    } finally {
      setCarregando(false);
      setAtualizando(false);
    }
  };

  useEffect(() => {
    fetchPedidos();

    const interval = setInterval(() => fetchPedidos(false), 30000);
    return () => clearInterval(interval);
  }, []);

  const getStatusDescricao = (status: string) => {
    const found = statusOptions.find(opt => opt.name === status);
    return found ? found.descricao : status;
  };

  const mapStatusToFilter = (status: string): string => {
    switch (status) {
      case 'EM_ANDAMENTO': return 'Em andamento';
      case 'ENTREGUE': return 'Compre novamente';
      case 'CANCELADO': return 'Cancelado';
      case 'PAGO': return 'Pago';
      default: return 'Em andamento';
    }
  };

  const pedidosFiltrados = filtro === 'Compre novamente'
    ? pedidos
    : pedidos.filter(p => filtro === 'Todos' || mapStatusToFilter(p.status) === filtro);

  const handleComprarNovamente = () => {
    navigate('/pagina-de-produtos');
  };

  // Função para limpar mensagens após alguns segundos
  useEffect(() => {
    if (mensagem) {
      const timer = setTimeout(() => {
        setMensagem('');
        setMensagemTipo('');
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [mensagem]);

  return (
    <div className="pagina-meus-pedidos">
      <div className="container">
        {/* Mensagem de feedback */}
        {mensagem && (
          <div className={`mensagem-feedback ${mensagemTipo}`}>
            {mensagem}
          </div>
        )}

        <div className="cabecalho-pedidos">
          <h1>Meus Pedidos</h1>
          <button className="btn-atualizar" onClick={() => fetchPedidos(false)} disabled={atualizando}>
            {atualizando ? '🔄 Atualizando...' : '🔄 Atualizar'}
          </button>
        </div>

        <div className="filtros-pedidos">
          {['Todos', 'Em andamento', 'Pago', 'Compre novamente', 'Cancelado'].map(opcao => (
            <button
              key={opcao}
              className={filtro === opcao ? 'ativo' : ''}
              onClick={() => setFiltro(opcao)}
            >
              {opcao}
            </button>
          ))}
        </div>

        {carregando ? (
          <div className="carregando">Carregando seus pedidos...</div>
        ) : pedidosFiltrados.length === 0 ? (
          <div className="sem-pedidos">
            <p>Você ainda não possui pedidos {filtro !== 'Todos' ? `com status "${filtro}"` : ''}.</p>
          </div>
        ) : (
          <div className="lista-pedidos">
            {pedidosFiltrados.map(pedido => (
              <div key={pedido.id} className={`card-pedido status-${mapStatusToFilter(pedido.status).toLowerCase().replace(/\s+/g, '-')}`}>
                <div className="info-pedido">
                  <div className="info-principal">
                    <p className="data-pedido">Pedido em {new Date(pedido.orderDate).toLocaleDateString('pt-BR')}</p>
                    <p className="status-pedido">{getStatusDescricao(pedido.status)}</p>
                    <div className="acoes-pedido">
                      <button className="btn-secundario" onClick={() => setModalProdutos({ aberto: true, pedido })}>Ver produtos</button>
                      <button className="btn-link" onClick={() => setModalDetalhes({ aberto: true, pedido })}>Detalhes</button>
                      {filtro === 'Compre novamente' && (
                        <button 
                          className="btn-primario" 
                          onClick={handleComprarNovamente}
                        >
                          Comprar novamente
                        </button>
                      )}
                    </div>
                  </div>
                  <div className="info-secundaria">
                    <p className="numero-pedido">Pedido #{pedido.id}</p>
                    <p className="total-pedido">Total: R$ {pedido.totalWithDelivery?.toFixed(2)}</p>
                    <div className="imagem-produto">
                      <img src={logo} alt="Logo Farmácia" className="logo-farmacia" />
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Modal Produtos */}
      {modalProdutos.aberto && modalProdutos.pedido && (
        <div className="modal-overlay" onClick={() => setModalProdutos({ aberto: false, pedido: null })}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Produtos do Pedido #{modalProdutos.pedido.id}</h2>
              <button className="btn-fechar" onClick={() => setModalProdutos({ aberto: false, pedido: null })}>×</button>
            </div>
            <div className="modal-body">
              {modalProdutos.pedido.items.length === 0 ? (
                <p>Nenhum produto encontrado neste pedido.</p>
              ) : modalProdutos.pedido.items.map((item, i) => (
                <div key={i} className="item-produto-modal">
                  <img src={item.productImageBase64 ? `data:image/jpeg;base64,${item.productImageBase64}` : logo} alt={item.productName} />
                  <div className="detalhes-produto-modal">
                    <h3>{item.productName}</h3>
                    <p>Qtd: {item.quantity}</p>
                    <p>Preço: R$ {item.price.toFixed(2)}</p>
                    <p>Subtotal: R$ {(item.price * item.quantity).toFixed(2)}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Modal Detalhes */}
      {modalDetalhes.aberto && modalDetalhes.pedido && (
        <div className="modal-overlay" onClick={() => setModalDetalhes({ aberto: false, pedido: null })}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Detalhes do Pedido #{modalDetalhes.pedido.id}</h2>
              <button className="btn-fechar" onClick={() => setModalDetalhes({ aberto: false, pedido: null })}>×</button>
            </div>
            <div className="modal-body">
              <p><strong>Data:</strong> {new Date(modalDetalhes.pedido.orderDate).toLocaleDateString('pt-BR')}</p>
              <p><strong>Status:</strong> {getStatusDescricao(modalDetalhes.pedido.status)}</p>
              <p><strong>Nome:</strong> {modalDetalhes.pedido.fullName}</p>
              <p><strong>Bairro:</strong> {modalDetalhes.pedido.neighborhoodName}</p>
              <p><strong>Produtos:</strong> {modalDetalhes.pedido.items.length}</p>
              <p><strong>Subtotal:</strong> R$ {modalDetalhes.pedido.totalValue?.toFixed(2)}</p>
              <p><strong>Entrega:</strong> R$ {modalDetalhes.pedido.deliveryTax?.toFixed(2)}</p>
              <p className="total-final"><strong>Total:</strong> R$ {modalDetalhes.pedido.totalWithDelivery?.toFixed(2)}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default MeusPedidos;