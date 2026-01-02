import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { IMaskInput } from 'react-imask';
import IndicadorProgresso from '../components/IndicadorProgresso';
import '../styles/SolicitarOrcamento.css';
import { api } from '../services/api';

/**
 * Componente para solicitar orçamento de produtos homeopáticos.
 * 
 * FUNCIONALIDADE DE EXCLUSÃO AUTOMÁTICA:
 * - Se o usuário sair da página sem finalizar o orçamento, o pedido homeopático é automaticamente excluído
 * - Detecta: fechamento da aba/navegador, navegação para outra página, botão voltar
 * - O pedido só é preservado se: o orçamento for enviado com sucesso, ou o usuário clicar em "Voltar ao carrinho"
 * - Usa o endpoint DELETE /orders/{id}/homeopathic que valida permissões do usuário
 */

interface OrderItem {
  productName: string;
  quantity: number;
  price: number;
  tipo?: string;
}

interface Order {
  id: number;
  orderDate: string;
  items: OrderItem[];
  totalValue: number;
  status: string;
  frete?: number;
}

interface EnderecoViaCEP {
  cep: string;
  logradouro: string;
  complemento: string;
  bairro: string;
  localidade: string;
  uf: string;
  erro?: boolean;
}

export default function SolicitarOrcamento() {
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(true);
  const [order, setOrder] = useState<Order | null>(null);
  const [continuar, setContinuar] = useState(false);
  const [enviado, setEnviado] = useState(false);
  const [frete, setFrete] = useState(0);
  const [orcamentoFinalizado, setOrcamentoFinalizado] = useState(false);
  
  // Número de WhatsApp para envio das mensagens
  const numeroWhatsApp = "553199217986";
  
  const [form, setForm] = useState({
    nome: '',
    email: '',
    celular: '',
    cep: '',
    rua: '',
    numero: '',
    complemento: '',
    observacao: '',
  });

  const [receita, setReceita] = useState<File | null>(null);
  const [mensagem, setMensagem] = useState('');
  const [enviando, setEnviando] = useState(false);
  const [buscandoCep, setBuscandoCep] = useState(false);
  const [mostrarConfirmacao, setMostrarConfirmacao] = useState(false);

  // Função para excluir pedido homeopático
  const excluirPedidoHomeopatico = async () => {
    if (orcamentoFinalizado || !order) return; // Não excluir se o orçamento foi finalizado ou não há pedido

    try {
      const token = localStorage.getItem('token');
      if (!token) return;

      console.log('Excluindo pedido homeopático ID:', order.id);
      
      // Usar o endpoint correto para excluir pedidos homeopáticos
      await api.delete(`/orders/${order.id}/homeopathic`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      
      console.log('Pedido homeopático excluído com sucesso');
      
    } catch (error) {
      console.error('Erro ao excluir pedido homeopático:', error);
    }
  };

  // Effect para detectar quando o usuário sai da página
  useEffect(() => {
    const handleBeforeUnload = (event: BeforeUnloadEvent) => {
      if (!orcamentoFinalizado) {
        // Mostrar aviso ao usuário
        event.preventDefault();
        event.returnValue = 'Seu pedido homeopático será perdido se você sair da página. Tem certeza?';
        
        // A exclusão será feita no cleanup se necessário
      }
    };

    const handlePopState = () => {
      if (!orcamentoFinalizado) {
        // Executa exclusão apenas se não foi finalizado
        excluirPedidoHomeopatico();
      }
    };

    // Adicionar event listeners apenas após um pequeno delay para evitar execução imediata
    const timer = setTimeout(() => {
      window.addEventListener('beforeunload', handleBeforeUnload);
      window.addEventListener('popstate', handlePopState);
    }, 1000); // 1 segundo de delay

    // Cleanup function quando o componente for desmontado
    return () => {
      clearTimeout(timer);
      window.removeEventListener('beforeunload', handleBeforeUnload);
      window.removeEventListener('popstate', handlePopState);
      
      // Se o componente for desmontado e o orçamento não foi finalizado, excluir o pedido
      // Mas apenas se não estivermos na página de orçamento ainda
      if (!orcamentoFinalizado && window.location.pathname !== '/solicitar-orcamento') {
        console.log('Componente desmontado, executando exclusão do pedido');
        excluirPedidoHomeopatico();
      }
    };
  }, [orcamentoFinalizado, order]);

  // Carregar dados do pedido do localStorage
  useEffect(() => {
    const carregarPedido = async () => {
      try {
        setLoading(true);
        
        // Buscar pedidos do usuário via API
        const token = localStorage.getItem('token');
        if (!token) {
          console.error('Token não encontrado, redirecionando para login');
          navigate('/login');
          return;
        }

        const response = await api.get('/orders/myOrders', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        const pedidos = response.data;
        console.log('Pedidos do usuário carregados:', pedidos);
        
        // Buscar o último pedido homeopático (mais recente)
        const pedidosHomeopaticos = pedidos.filter((pedido: any) => 
          pedido.orderType === 'HOMEOPATICO'
        );
        
        console.log('Pedidos homeopáticos filtrados:', pedidosHomeopaticos);
        
        if (pedidosHomeopaticos.length === 0) {
          console.error('Nenhum pedido homeopático encontrado');
          setMensagem('Nenhum pedido homeopático encontrado. Adicione produtos ao carrinho e finalize antes de solicitar orçamento.');
          setLoading(false);
          return;
        }
        
        // Pegar o pedido mais recente (último)
        const ultimoPedidoHomeopatico = pedidosHomeopaticos.sort((a: any, b: any) => 
          new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime()
        )[0];
        
        console.log('Último pedido homeopático:', ultimoPedidoHomeopatico);
        
        // Verificar se deve continuar para pagamento após orçamento
        const params = new URLSearchParams(location.search);
        const deveContinar = params.get('continuar') === 'true';
        setContinuar(deveContinar);
        
        // Carregar frete - priorizar o frete do pedido, depois o salvo no localStorage
        let freteCalculado = 0;
        
        if (ultimoPedidoHomeopatico.deliveryTax && ultimoPedidoHomeopatico.deliveryTax > 0) {
          // Usar frete do pedido se disponível
          freteCalculado = ultimoPedidoHomeopatico.deliveryTax;
          console.log('Usando frete do pedido:', freteCalculado);
        } else {
          // Fallback para frete salvo no localStorage
          const freteSalvo = localStorage.getItem('freteCalculado');
          if (freteSalvo) {
            freteCalculado = parseFloat(freteSalvo);
            console.log('Usando frete salvo no localStorage:', freteCalculado);
          }
        }
        
        setFrete(freteCalculado);
        
        // Converter OrderDTO para o formato esperado
        // totalValue do backend já vem SEM o frete (conforme definido no OrderService.setCommonOrderFields)
        const pedidoReal: Order = {
          id: ultimoPedidoHomeopatico.id,
          orderDate: ultimoPedidoHomeopatico.orderDate,
          items: ultimoPedidoHomeopatico.items.map((item: any) => ({
            productName: item.productName,
            quantity: item.quantity,
            price: item.price,
            tipo: 'HOMEOPATICO'
          })),
          totalValue: ultimoPedidoHomeopatico.totalValue, // Já vem sem o frete
          status: ultimoPedidoHomeopatico.status,
          frete: freteCalculado
        };
        
        console.log('Pedido criado com dados reais do último pedido:', pedidoReal);
        setOrder(pedidoReal);
        
        // Preencher observação salva (se houver)
        const observacaoSalva = localStorage.getItem('observacaoPedido');
        if (observacaoSalva) {
          setForm(prev => ({ ...prev, observacao: observacaoSalva }));
        }
        
      } catch (err) {
        console.error('Erro ao carregar pedidos:', err);
        setMensagem('Erro ao carregar pedido homeopático. Tente novamente.');
      } finally {
        setLoading(false);
      }
    };

    carregarPedido();
  }, [location.search, navigate]);

  // Função para buscar endereço pelo CEP
  const buscarEnderecoPorCEP = async (cep: string) => {
    // Remover caracteres não numéricos
    const cepLimpo = cep.replace(/\D/g, '');
    
    // Verificar se o CEP tem 8 dígitos
    if (cepLimpo.length !== 8) return;
    
    setBuscandoCep(true);
    setMensagem('');
    
    try {
      const response = await fetch(`https://viacep.com.br/ws/${cepLimpo}/json/`);
      const data: EnderecoViaCEP = await response.json();
      
      if (data.erro) {
        setMensagem('CEP não encontrado. Verifique o número informado.');
        return;
      }
      
      // Preencher os campos com os dados retornados
      setForm(prev => ({
        ...prev,
        rua: data.logradouro,
        complemento: data.complemento || prev.complemento
      }));
      
    } catch (error) {
      console.error('Erro ao buscar CEP:', error);
      setMensagem('Erro ao buscar o CEP. Tente novamente ou preencha manualmente.');
    } finally {
      setBuscandoCep(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleCepChange = (value: string) => {
    setForm({ ...form, cep: value });
    
    // Se o CEP estiver completo (com ou sem máscara), buscar o endereço
    if (value.replace(/\D/g, '').length === 8) {
      buscarEnderecoPorCEP(value);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setReceita(e.target.files[0]);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!receita) {
      setMensagem('Por favor, anexe a receita médica.');
      return;
    }

    setEnviando(true);
    
    try {
      // Limpar o telefone removendo caracteres não numéricos
      const cleanPhone = form.celular.replace(/\D/g, '');

      // Criar FormData para enviar o arquivo e os dados
      const formData = new FormData();
      formData.append('fullName', form.nome);
      formData.append('phone', cleanPhone);
      formData.append('email', form.email);
      formData.append('observation', form.observacao);
      formData.append('medicalPrescription', receita);
      formData.append('cep', form.cep);
      formData.append('rua', form.rua);
      formData.append('numero', form.numero);
      formData.append('complemento', form.complemento);

      // Enviar o orçamento para a API
      const response = await api.post(`/order-quotes/${order?.id}`, formData);

      // Marcar que o orçamento foi finalizado para não excluir o pedido
      setOrcamentoFinalizado(true);

      // Preparar mensagem para WhatsApp
      const listaProdutos = order?.items
        .map((item) => `- ${item.productName}: Quantidade: ${item.quantity}`)
        .join("\n");

      const valorSubtotal = (order?.totalValue || 0).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL",
      });

      console.log('Valor do frete no momento da mensagem:', frete);
      console.log('Order totalValue:', order?.totalValue);
      
      const freteParaCalculo = (frete > 0 && !isNaN(frete)) ? frete : 20; // Default de R$ 20,00 se não houver frete
      const valorFrete = freteParaCalculo.toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL",
      });
      
      console.log('Frete usado no cálculo:', freteParaCalculo);

      const valorTotalComFrete = (order?.totalValue || 0) + freteParaCalculo;
      const valorTotalFormatado = valorTotalComFrete.toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL",
      });
      
      console.log('Valor total com frete:', valorTotalComFrete);

      const mensagemWhatsApp = encodeURIComponent(
        `Olá! Gostaria de solicitar um orçamento para os seguintes produtos homeopáticos:\n\n${listaProdutos}\n\nValor dos produtos: ${valorSubtotal}\nFrete: ${valorFrete}\nValor total estimado: ${valorTotalFormatado}\n\nDados do cliente:\nNome: ${form.nome}\nE-mail: ${form.email}\nCelular: ${form.celular}\nCEP: ${form.cep}\nRua: ${form.rua}\nNúmero: ${form.numero}\nComplemento: ${form.complemento || "Não informado"}\n\nObservações: ${form.observacao || "Nenhuma"}`
      );

      // Mostrar tela de confirmação
      setEnviado(true);
      
      // Aguardar 3 segundos antes de redirecionar
      setTimeout(() => {
        // Verificar se deve continuar para pagamento (carrinho misto)
        if (continuar) {
          // Salvar informações do formulário para uso posterior
          localStorage.setItem('clienteNome', form.nome);
          localStorage.setItem('clienteEmail', form.email);
          localStorage.setItem('clienteCelular', form.celular);
          
          // Abrir WhatsApp em nova aba
          window.open(`https://wa.me/${numeroWhatsApp}?text=${mensagemWhatsApp}`, '_blank');
          
          // Redirecionar para página de pagamento PIX
          const pedidoRevenda = localStorage.getItem('pedidoRevenda');
          if (pedidoRevenda) {
            const pedido = JSON.parse(pedidoRevenda);
            navigate(`/pagamento-pix?orderId=${pedido.id}`);
          } else {
            // Fallback se não encontrar o pedido de revenda
            navigate('/pagamento-pix');
          }
        } else {
          // Limpar o pedido do localStorage
          localStorage.removeItem('pedidoAtual');
          localStorage.removeItem('pedidoHomeopatico');
          localStorage.removeItem('pedidoRevenda');
          localStorage.removeItem('carrinhoMisto');
          
          // Redirecionar para WhatsApp
          window.location.href = `https://wa.me/${numeroWhatsApp}?text=${mensagemWhatsApp}`;
        }
      }, 3000);
      
    } catch (error) {
      console.error('Erro ao enviar orçamento:', error);
      setMensagem('Erro ao enviar orçamento. Tente novamente.');
      setEnviando(false);
    }
  };

  const voltarAoCarrinho = async () => {
    // Mostrar modal de confirmação
    setMostrarConfirmacao(true);
  };

  const confirmarVoltarAoCarrinho = async () => {
    setMostrarConfirmacao(false);
    
    // Marcar que o usuário escolheu voltar para não excluir o pedido automaticamente pelos event listeners
    setOrcamentoFinalizado(true);
    
    try {
      // Se há um pedido homeopático, precisamos recriar o carrinho
      const carrinhoMisto = localStorage.getItem('carrinhoMisto') === 'true';
      let pedidoParaRecriar = null;

      if (carrinhoMisto) {
        const pedidoHomeopaticoSalvo = localStorage.getItem('pedidoHomeopatico');
        if (pedidoHomeopaticoSalvo) {
          pedidoParaRecriar = JSON.parse(pedidoHomeopaticoSalvo);
        }
      } else {
        const pedidoAtualSalvo = localStorage.getItem('pedidoAtual');
        if (pedidoAtualSalvo) {
          pedidoParaRecriar = JSON.parse(pedidoAtualSalvo);
        }
      }

      if (pedidoParaRecriar && pedidoParaRecriar.items) {
        // Recriar carrinho com os itens do pedido homeopático
        const carrinhoItems = pedidoParaRecriar.items.map((item: OrderItem, index: number) => ({
          cartItemId: Math.floor(Math.random() * 10000) + index,
          productId: Math.floor(Math.random() * 10000) + index,
          productName: item.productName,
          productPrice: item.price,
          quantity: item.quantity,
          productType: 'HOMEOPATICO' as const
        }));

        // Salvar no localStorage para fallback
        localStorage.setItem('carrinho', JSON.stringify(carrinhoItems));
      }

      // Excluir o pedido já que o usuário confirmou que quer voltar ao carrinho
      await excluirPedidoHomeopatico();
      
    } catch (error) {
      console.error('Erro ao processar volta ao carrinho:', error);
    }
    
    navigate('/carrinho-de-compras');
  };

  const cancelarVoltarAoCarrinho = () => {
    setMostrarConfirmacao(false);
  };

  if (loading) {
    return (
      <div className="pagina-solicitar-orcamento">
        <IndicadorProgresso passos={3} passoAtual={2} />
        <div className="carregando">Carregando informações do pedido...</div>
      </div>
    );
  }

  if (enviado) {
    return (
      <div className="pagina-solicitar-orcamento">
        <IndicadorProgresso passos={3} passoAtual={2} />
        <div className="confirmacao-envio">
          <div className="icone-sucesso">✓</div>
          <h2>Orçamento Enviado com Sucesso!</h2>
          <p>Seu pedido de orçamento para produtos homeopáticos foi registrado.</p>
          <p>Número do pedido: <strong>{order?.id || 'N/A'}</strong></p>
          <p>Você será redirecionado para o WhatsApp para finalizar seu pedido...</p>
          {continuar && (
            <p>Em seguida, você será redirecionado para a página de pagamento dos produtos de revenda.</p>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="pagina-solicitar-orcamento">
      <IndicadorProgresso passos={3} passoAtual={2} />
      <h1>Solicitar Orçamento</h1>
      
      {continuar && (
        <div className="alerta-fluxo-misto">
          <p>
            <strong>Atenção:</strong> Após solicitar o orçamento para os produtos homeopáticos, 
            você será redirecionado para finalizar a compra dos produtos de revenda.
          </p>
        </div>
      )}
      
      <div className="conteudo-orcamento">
        <div className="coluna-formulario">
          <form className="formulario-orcamento" onSubmit={handleSubmit}>
            <div className="secao-formulario">
              <h3>Dados Pessoais</h3>
              
              <div className="campo">
                <label htmlFor="nome">Nome Completo*</label>
                <input 
                  type="text" 
                  id="nome" 
                  name="nome" 
                  value={form.nome} 
                  onChange={handleChange} 
                  placeholder="Nome Completo"
                  required 
                />
              </div>
              
              <div className="campo">
                <label htmlFor="email">E-mail*</label>
                <input 
                  type="email" 
                  id="email" 
                  name="email" 
                  value={form.email} 
                  onChange={handleChange} 
                  placeholder="nome@email.com"
                  required 
                />
              </div>
              
              <div className="campo">
                <label htmlFor="celular">Celular*</label>
                <IMaskInput
                  id="celular"
                  name="celular"
                  value={form.celular}
                  onAccept={(value: any) => setForm({ ...form, celular: value })}
                  mask="(00) 00000-0000"
                  placeholder="(00) 00000-0000"
                  className="input-mask"
                  required
                />
              </div>
              
              <div className="campo">
                <label htmlFor="cep">CEP* {buscandoCep && <span className="buscando-cep">Buscando...</span>}</label>
                <IMaskInput
                  id="cep"
                  name="cep"
                  value={form.cep}
                  onAccept={handleCepChange}
                  mask="00000-000"
                  placeholder="00000-000"
                  className="input-mask"
                  required
                />
              </div>
              
              <div className="campo">
                <label htmlFor="rua">Rua*</label>
                <input 
                  type="text" 
                  id="rua" 
                  name="rua" 
                  value={form.rua} 
                  onChange={handleChange} 
                  placeholder="Nome da rua"
                  required 
                />
              </div>
              
              <div className="campo-duplo">
                <div className="campo">
                  <label htmlFor="numero">Número*</label>
                  <input 
                    type="text" 
                    id="numero" 
                    name="numero" 
                    value={form.numero} 
                    onChange={handleChange} 
                    placeholder="Número"
                    required 
                  />
                </div>
                
                <div className="campo">
                  <label htmlFor="complemento">Complemento</label>
                  <input 
                    type="text" 
                    id="complemento" 
                    name="complemento" 
                    value={form.complemento} 
                    onChange={handleChange} 
                    placeholder="Apto, Bloco, etc."
                  />
                </div>
              </div>
            </div>
            
            <div className="secao-formulario">
              <h3>Receita Médica</h3>
              
              <div className="campo-arquivo">
                <label htmlFor="receita">Anexar Receita*</label>
                <div className="area-upload">
                  <input
                    type="file"
                    id="receita"
                    accept="image/*,application/pdf"
                    onChange={handleFileChange}
                    required
                  />
                  <p className="info-upload">Envie imagens/arquivos nos formatos JPEG, JPG, PNG, PDF</p>
                </div>
              </div>
              
              <div className="campo">
                <label htmlFor="observacao">Observação</label>
                <textarea
                  id="observacao"
                  name="observacao"
                  value={form.observacao}
                  onChange={handleChange}
                  rows={4}
                  placeholder="Utilize este espaço para escrever informações adicionais sobre a(s) receita(s) que você está nos enviando."
                  maxLength={200}
                />
                <div className="contador-caracteres">{form.observacao.length}/200</div>
              </div>
            </div>
            
            <div className="acoes-formulario">
              <button 
                type="button" 
                className="btn-outline" 
                onClick={voltarAoCarrinho}
              >
                Voltar ao carrinho
              </button>
              <button 
                type="submit" 
                className="btn-primary"
                disabled={enviando}
              >
                {enviando ? 'Enviando...' : continuar ? 'Solicitar e Continuar' : 'Enviar Orçamento'}
              </button>
            </div>
            
            {mensagem && <div className="mensagem-alerta">{mensagem}</div>}
          </form>
        </div>
        
        <div className="coluna-resumo">
          <div className="resumo-pedido">
            <h3>Resumo do Pedido</h3>
            <div className="itens-pedido">
              {order?.items.map((item, index) => (
                <div className="item-pedido" key={index}>
                  <div className="item-info">
                    <span className="item-nome">{item.productName}</span>
                    <span className="item-qtd">x{item.quantity}</span>
                  </div>
                  <span className="item-preco">
                    R$ {(item.price * item.quantity).toFixed(2)}
                  </span>
                </div>
              ))}
            </div>
            
            <div className="linha-divisoria"></div>
            
            <div className="subtotal">
              <span>Subtotal:</span>
              <span>
                R$ {order?.totalValue?.toFixed(2) || '0,00'}
              </span>
            </div>
            
            <div className="frete-linha">
              <span>Frete:</span>
              <span>
                {(frete > 0 && !isNaN(frete)) ? `R$ ${frete.toFixed(2)}` : 'R$ 20,00'}
              </span>
            </div>
            
            <div className="total-pedido">
              <span>Total:</span>
              <span className="valor-total">
                R$ {((order?.totalValue || 0) + (frete > 0 && !isNaN(frete) ? frete : 20)).toFixed(2)}
              </span>
            </div>
          </div>
          
          <div className="info-orcamento">
            <h3>Informações Importantes</h3>
            <ul>
              <li>Produtos homeopáticos são manipulados sob medida.</li>
              <li>Após o envio do orçamento, entraremos em contato via WhatsApp.</li>
              <li>O prazo de manipulação é de até 48 horas após a confirmação do pagamento.</li>
              <li>Receita médica é obrigatória para produtos homeopáticos.</li>
              <li>Frete: R$ 20,00 para {localStorage.getItem('bairroSelecionado') || 'bairro selecionado'}.</li>
            </ul>
          </div>
        </div>
      </div>
      
      {/* Modal de Confirmação */}
      {mostrarConfirmacao && (
        <div className="modal-overlay">
          <div className="modal-confirmacao">
            <div className="modal-header">
              <h3>Confirmar Ação</h3>
            </div>
            <div className="modal-body">
              <p>Essa ação irá desfazer o seu pedido homeopático. Deseja continuar?</p>
            </div>
            <div className="modal-footer">
              <button 
                type="button" 
                className="btn-outline" 
                onClick={cancelarVoltarAoCarrinho}
              >
                Cancelar
              </button>
              <button 
                type="button" 
                className="btn-primary" 
                onClick={confirmarVoltarAoCarrinho}
              >
                Sim, Continuar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}