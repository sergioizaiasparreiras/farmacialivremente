import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaCheck } from 'react-icons/fa';
import '../../styles/PaginaPedidosAdmin.css';
import { api } from '../../services/api';

interface PedidoItem {
  productName: string;
  quantity: number;
  price: number;
  productImageBase64?: string;
}

interface Pedido {
  id: number;
  orderDate: string;
  fullName: string;
  items: PedidoItem[];
  status: string;
  totalValue: number;
  deliveryTax: number;
  totalWithDelivery: number;
  neighborhoodName: string;
}

interface Notificacao {
  id: number;
  tipo: 'sucesso' | 'erro';
  mensagem: string;
}

const PaginaPedidosAdmin: React.FC = () => {
  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [pedidosExibidos, setPedidosExibidos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [statusAtualizado, setStatusAtualizado] = useState<{ [id: number]: string }>({});
  const [notificacoes, setNotificacoes] = useState<Notificacao[]>([]);
  const [filtroStatus, setFiltroStatus] = useState<string>('todos');
  const [dataInicial, setDataInicial] = useState<string>('');
  const [dataFinal, setDataFinal] = useState<string>('');
  const [ordenacao, setOrdenacao] = useState<string>('recente');
  const [statusOptions, setStatusOptions] = useState<{ name: string, descricao: string }[]>([]);
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

  useEffect(() => {
    const fetchPedidos = async () => {
      try {
        setLoading(true);
        
        const token = localStorage.getItem('token');
        if (!token) return navigate('/login');


        const { data } = await api.get('/orders/allOrders', {
          headers: { Authorization: `Bearer ${token}` },
        });
        
        setPedidos(data);
        setPedidosExibidos(data);
        
        // Inicializar status atualizado com status atual
        const statusInicial: { [id: number]: string } = {};
        data.forEach((pedido: Pedido) => statusInicial[pedido.id] = pedido.status);
        setStatusAtualizado(statusInicial);
        
        setLoading(false);
      } catch (error) {
        console.error('Erro ao buscar pedidos:', error);
        setLoading(false);
      }
    };

    fetchPedidos();
  }, [navigate]);

  // Aplicar filtros sempre que os critérios mudarem
  useEffect(() => {
    let resultado = [...pedidos];

    // Filtro por status
    if (filtroStatus !== 'todos') {
      resultado = resultado.filter(pedido => pedido.status === filtroStatus);
    }

    // Filtro por data inicial
    if (dataInicial) {
      resultado = resultado.filter(pedido => {
        const dataPedido = new Date(pedido.orderDate);
        const dataFiltro = new Date(dataInicial);
        return dataPedido >= dataFiltro;
      });
    }

    // Filtro por data final
    if (dataFinal) {
      resultado = resultado.filter(pedido => {
        const dataPedido = new Date(pedido.orderDate);
        const dataFiltro = new Date(dataFinal);
        dataFiltro.setHours(23, 59, 59, 999); // Incluir todo o dia final
        return dataPedido <= dataFiltro;
      });
    }

    // Aplicar ordenação
    if (ordenacao === 'recente') {
      resultado.sort((a, b) => new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime());
    } else if (ordenacao === 'antigo') {
      resultado.sort((a, b) => new Date(a.orderDate).getTime() - new Date(b.orderDate).getTime());
    }

    setPedidosExibidos(resultado);
  }, [pedidos, filtroStatus, dataInicial, dataFinal, ordenacao]);

  const adicionarNotificacao = (tipo: 'sucesso' | 'erro', mensagem: string) => {
    const id = Date.now();
    const novaNotificacao: Notificacao = { id, tipo, mensagem };
    
    setNotificacoes(prev => [...prev, novaNotificacao]);
    
    // Remove a notificação após 5 segundos
    setTimeout(() => {
      setNotificacoes(prev => prev.filter(notif => notif.id !== id));
    }, 5000);
  };

  const removerNotificacao = (id: number) => {
    setNotificacoes(prev => prev.filter(notif => notif.id !== id));
  };

  const handleStatusChange = (id: number, novoStatus: string) => {
    setStatusAtualizado((prev: { [id: number]: string }) => ({
      ...prev,
      [id]: novoStatus
    }));
  };

  // Função helper para verificar se há mudança no status
  const temMudancaStatus = (pedido: Pedido): boolean => {
    const statusSelecionado = statusAtualizado[pedido.id];
    return statusSelecionado !== undefined && statusSelecionado !== pedido.status;
  };

  const handleConfirmar = async (id: number) => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        adicionarNotificacao('erro', 'Token não encontrado. Faça login novamente.');
        return navigate('/login');
      }

      await api.put(`/orders/${id}/status`, null, {
        params: { status: statusAtualizado[id] }
      });

      // Atualizar a lista local de pedidos
      setPedidos(prevPedidos => 
        prevPedidos.map(pedido => 
          pedido.id === id ? { ...pedido, status: statusAtualizado[id] } : pedido
        )
      );

      // Sincronizar o statusAtualizado com o novo status confirmado
      setStatusAtualizado(prev => ({
        ...prev,
        [id]: statusAtualizado[id]
      }));

      const statusDescricao = statusOptions.find(opt => opt.name === statusAtualizado[id])?.descricao || statusAtualizado[id];
      adicionarNotificacao('sucesso', `Status do pedido #${id} atualizado para ${statusDescricao} com sucesso!`);
    } catch (error) {
      console.error('Erro ao atualizar status:', error);
      adicionarNotificacao('erro', 'Erro ao atualizar status do pedido. Tente novamente.');
    }
  };

  return (
    <div className="produtos-container">
      {/* Container de Notificações */}
      {notificacoes.length > 0 && (
        <div className="notificacoes-container">
          {notificacoes.map((notificacao) => (
            <div
              key={notificacao.id}
              className={`notificacao ${notificacao.tipo === 'sucesso' ? 'notificacao-sucesso' : 'notificacao-erro'}`}
            >
              <span className="notificacao-mensagem">{notificacao.mensagem}</span>
              <button
                className="notificacao-fechar"
                onClick={() => removerNotificacao(notificacao.id)}
              >
                ×
              </button>
            </div>
          ))}
        </div>
      )}

      <div className="cabecalho-pagina">
        <h1>Lista dos Pedidos</h1>
        <p className="subtitulo">Gerencie os pedidos realizados no sistema</p>
      </div>

      {/* Filtros de pedidos */}
      <div className="controles-filtros">
        <div className="filtros-linha-1">
          <div className="filtro-grupo">
            <label htmlFor="filtro-status">Status:</label>
            <select 
              id="filtro-status"
              value={filtroStatus} 
              onChange={(e) => setFiltroStatus(e.target.value)}
              className="select-filtro"
            >
              <option value="todos">Todos</option>
              {statusOptions.map(opt => (
                <option key={opt.name} value={opt.name}>{opt.descricao}</option>
              ))}
            </select>
          </div>
          
          <div className="filtro-grupo">
            <label htmlFor="ordenacao">Ordenação:</label>
            <select 
              id="ordenacao"
              value={ordenacao} 
              onChange={(e) => setOrdenacao(e.target.value)}
              className="select-filtro"
            >
              <option value="recente">Mais recente</option>
              <option value="antigo">Mais antigo</option>
            </select>
          </div>
        </div>
        
        <div className="filtros-linha-2">
          <div className="filtro-grupo">
            <label htmlFor="data-inicial">Data inicial:</label>
            <input
              id="data-inicial"
              type="date"
              value={dataInicial}
              onChange={(e) => setDataInicial(e.target.value)}
              className="input-data"
            />
          </div>
          
          <div className="filtro-grupo">
            <label htmlFor="data-final">Data final:</label>
            <input
              id="data-final"
              type="date"
              value={dataFinal}
              onChange={(e) => setDataFinal(e.target.value)}
              className="input-data"
            />
          </div>
          
          <div className="filtro-grupo">
            <button 
              className="btn-limpar-filtros"
              onClick={() => {
                setFiltroStatus('todos');
                setDataInicial('');
                setDataFinal('');
                setOrdenacao('recente');
              }}
            >
              Limpar Filtros
            </button>
          </div>
        </div>
      </div>

      {/* Contador de resultados */}
      {!loading && (
        <div className="contador-pedidos">
          Mostrando {pedidosExibidos.length} de {pedidos.length} pedidos
        </div>
      )}

      {loading ? (
        <div className="loading">Carregando pedidos...</div>
      ) : pedidos.length === 0 ? (
        <div className="loading">Nenhum pedido encontrado</div>
      ) : (
        <div className="produtos-lista">
          {pedidosExibidos.map((pedido: Pedido) => (
            <div className="produto-item" key={pedido.id}>
              <div className="produto-info">
                <div className="produto-nome">
                  {new Date(pedido.orderDate).toLocaleDateString('pt-BR')} - {pedido.fullName}
                </div>
                <div className="produto-lista">
                  {pedido.items && pedido.items.length > 0 ? (
                    pedido.items.map((item, index) => (
                      <span key={index}>
                        {item.quantity}x {item.productName}
                        {index < pedido.items.length - 1 ? ', ' : ''}
                      </span>
                    ))
                  ) : (
                    <span>Nenhum item encontrado</span>
                  )}
                </div>
                <div className="produto-total">
                  Total: R$ {pedido.totalWithDelivery?.toFixed(2) || pedido.totalValue?.toFixed(2)}
                  {pedido.neighborhoodName && ` | ${pedido.neighborhoodName}`}
                </div>
              </div>
              
              <div className="produto-acoes">
                <select
                  className="select-status"
                  value={statusAtualizado[pedido.id] || pedido.status}
                  onChange={(e) => handleStatusChange(pedido.id, e.target.value)}
                >
                  {statusOptions.map(opt => (
                    <option key={opt.name} value={opt.name}>{opt.descricao}</option>
                  ))}
                </select>
                <button
                  className="btn-confirmarStatus"
                  onClick={() => handleConfirmar(pedido.id)}
                  disabled={!temMudancaStatus(pedido)}
                  title={!temMudancaStatus(pedido) ? 'Nenhuma alteração para confirmar' : 'Confirmar alteração de status'}
                >
                  <FaCheck />
                  Confirmar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default PaginaPedidosAdmin;