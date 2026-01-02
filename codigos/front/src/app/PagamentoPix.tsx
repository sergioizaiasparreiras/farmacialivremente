import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import IndicadorProgresso from '../components/IndicadorProgresso';
import { api } from '../services/api';

interface PedidoItem {
  productName: string;
  quantity: number;
  price: number;
}

interface Pedido {
  id: number;
  items: PedidoItem[];
  totalWithDelivery: number;
  deliveryTax: number;
  neighborhoodName: string;
  status: string;
}

const PagamentoPix = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [order, setOrder] = useState<Pedido | null>(null);
  const [loading, setLoading] = useState(true);
  const [statusPagamento, setStatusPagamento] = useState<'pendente' | 'confirmado' | 'erro'>('pendente');
  const [error, setError] = useState('');
  const orderId = new URLSearchParams(location.search).get('orderId') as string;

  // Carrega o pedido e status inicial
  useEffect(() => {
    const carregarPedido = async () => {
      if (!orderId) {
        setError('ID do pedido não encontrado na URL.');
        setLoading(false);
        return;
      }
      try {
        const response = await api.get(`/orders/${orderId}`);
        setOrder(response.data);
        if (response.data.status === 'RECUSADO') {
          setStatusPagamento('erro');
        } else {
          setStatusPagamento('pendente');
        }
      } catch (e) {
        setError('Erro ao buscar o pedido. Tente novamente.');
      } finally {
        setLoading(false);
      }
    };
    carregarPedido();
  }, [orderId]);

  // Simula o delay de processamento do pagamento
  useEffect(() => {
    if (order && order.status === 'PAGO' && statusPagamento === 'pendente') {
      const timeout = setTimeout(() => {
        setStatusPagamento('confirmado');
      }, 4000); // 4 segundos de delay
      return () => clearTimeout(timeout);
    }
  }, [order, statusPagamento]);

  // Polling para atualizar status do pagamento (mantido para casos de erro)
  useEffect(() => {
    if (!orderId || statusPagamento !== 'pendente') return;
    const interval = setInterval(async () => {
      try {
        const res = await api.get(`/orders/${orderId}`);
        if (res.data.status === 'RECUSADO') {
          setStatusPagamento('erro');
        }
      } catch {}
    }, 5000);
    return () => clearInterval(interval);
  }, [orderId, statusPagamento]);

  const voltarParaLoja = () => navigate('/pagina-de-produtos');
  const voltarAoCarrinho = () => navigate('/carrinho-de-compras');
  const verMeusPedidos = () => navigate('/meus-pedidos');

  if (loading) {
    return (
      <div className="pagina-pagamento-pix">
        <IndicadorProgresso passos={3} passoAtual={2} />
        <div className="carregando">Carregando informações do pagamento...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="pagina-pagamento-pix">
        <IndicadorProgresso passos={3} passoAtual={2} />
        <div className="erro-container">
          <h2>Erro ao carregar pagamento</h2>
          <p>{error}</p>
          <button className="btn-primary" onClick={voltarAoCarrinho}>
            Voltar ao carrinho
          </button>
        </div>
      </div>
    );
  }

  if (!order) {
    return (
      <div className="pagina-pagamento-pix">
        <IndicadorProgresso passos={3} passoAtual={2} />
        <div className="erro-container">
          <h2>Pedido não encontrado</h2>
          <p>Não foi possível encontrar os detalhes do seu pedido.</p>
          <button className="btn-primary" onClick={voltarAoCarrinho}>
            Voltar ao carrinho
          </button>
        </div>
      </div>
    );
  }

  // Status: Pagamento confirmado
  if (statusPagamento === 'confirmado') {
    return (
      <div className="pagina-pagamento-pix">
        <IndicadorProgresso passos={3} passoAtual={2} />
        <div className="status-animado sucesso">
          <span className="icone-status">
            {/* Ícone de check animado */}
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
              <circle cx="32" cy="32" r="32" fill="#27ae60" opacity="0.15"/>
              <path d="M20 34L29 43L44 25" stroke="#27ae60" strokeWidth="4" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </span>
          <h1>Pagamento confirmado!</h1>
          <p>Seu pedido #{order.id} foi confirmado e está sendo processado.</p>
          <div className="acoes-sucesso">
            <button className="btn-primary" onClick={voltarParaLoja}>
              Voltar para a loja
            </button>
            <button className="btn-outline" onClick={verMeusPedidos}>
              Ver meus pedidos
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Status: Pagamento recusado
  if (statusPagamento === 'erro') {
    return (
      <div className="pagina-pagamento-pix">
        <IndicadorProgresso passos={3} passoAtual={2} />
        <div className="status-animado erro">
          <span className="icone-status">
            {/* Ícone de X animado */}
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
              <circle cx="32" cy="32" r="32" fill="#e74c3c" opacity="0.15"/>
              <path d="M24 24L40 40M40 24L24 40" stroke="#e74c3c" strokeWidth="4" strokeLinecap="round"/>
            </svg>
          </span>
          <h1>Pagamento não aprovado</h1>
          <p>Não foi possível confirmar seu pagamento.</p>
          <div className="acoes-erro">
            <button className="btn-primary" onClick={voltarAoCarrinho}>
              Voltar ao carrinho
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Status: Aguardando pagamento
  return (
    <div className="pagina-pagamento-pix">
      <IndicadorProgresso passos={3} passoAtual={2} />
      <h1>Aguardando confirmação do pagamento...</h1>
      <p>Assim que o pagamento for aprovado, você será notificado aqui.</p>
      <div className="resumo-pedido">
        <h3>Resumo do Pedido</h3>
        <div className="itens-pedido">
          {order.items && order.items.map((item, idx) => (
            <div className="item-pedido" key={item.productName + idx}>
              <span>{item.productName} x{item.quantity}</span>
              <span>R$ {(item.price * item.quantity).toFixed(2)}</span>
            </div>
          ))}
        </div>
        <div className="linha-divisoria"></div>
        <div className="subtotal">
          <span>Subtotal:</span>
          <span>R$ {(order.totalWithDelivery - order.deliveryTax).toFixed(2)}</span>
        </div>
        <div className="taxa-entrega">
          <span>Taxa de entrega ({order.neighborhoodName}):</span>
          <span>R$ {order.deliveryTax.toFixed(2)}</span>
        </div>
        <div className="total-pedido">
          <span>Total:</span>
          <span className="valor-total">
            R$ {order.totalWithDelivery.toFixed(2)}
          </span>
        </div>
      </div>
    </div>
  );
};

export default PagamentoPix;