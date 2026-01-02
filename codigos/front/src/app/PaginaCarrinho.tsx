import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import IndicadorProgresso from '../components/IndicadorProgresso';
import ItemProduto from '../components/ItemProduto';
import InformacaoEntrega from '../components/InformacaoEntrega';
import '../styles/PaginaCarrinho.css';
import { api } from '../services/api';

interface Produto {
  cartItemId: number;
  productId: number;
  productName: string;
  productPrice: number;
  quantity: number;
  productType: 'REVENDA' | 'HOMEOPATICO';
  productPhotoBase64?: string;
}

interface Bairro {
  name: string;
  tax: number;
}

const PaginaCarrinho = () => {
  const navigate = useNavigate();
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [tipoCarrinho, setTipoCarrinho] = useState<'REVENDA' | 'HOMEOPATICO' | 'MISTO' | null>(null);
  const [bairroSelecionado, setBairroSelecionado] = useState('');
  const [bairrosDisponiveis, setBairrosDisponiveis] = useState<Bairro[]>([]);
  const [observacao, setObservacao] = useState('');
  const [frete, setFrete] = useState(0);
  const [loading, setLoading] = useState(true);
  const [isProcessing, setIsProcessing] = useState(false);
  const [error, setError] = useState('');

  const carregarBairros = async () => {
    try {
      const response = await api.get('/neighborhoods');
      setBairrosDisponiveis(response.data);
    } catch (error) {
      console.error('Erro ao carregar bairros:', error);
      setBairrosDisponiveis([
        { name: 'Planalto', tax: 5 },
        { name: 'Serra', tax: 7 },
        { name: 'Boa Vista', tax: 10 }
      ]);
    }
  };

  const carregarCarrinho = async () => {
    try {
      setLoading(true);
      const response = await api.get('/cart/items');
      const produtosDoCarrinho: Produto[] = Array.isArray(response.data) ? response.data : [];
      setProdutos(produtosDoCarrinho);

      if (produtosDoCarrinho.length > 0) {
        const temHomeopaticos = produtosDoCarrinho.some(p => p.productType === 'HOMEOPATICO');
        const temRevenda = produtosDoCarrinho.some(p => p.productType === 'REVENDA');

        if (temHomeopaticos && !temRevenda) setTipoCarrinho('HOMEOPATICO');
        else if (!temHomeopaticos && temRevenda) setTipoCarrinho('REVENDA');
        else if (temHomeopaticos && temRevenda) setTipoCarrinho('MISTO');
        else setTipoCarrinho(null);
      } else {
        setTipoCarrinho(null);
      }
    } catch (err) {
      console.error('Erro ao carregar carrinho:', err);
      setError('Erro ao carregar o carrinho. Tente novamente mais tarde.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarCarrinho();
    carregarBairros();
  }, []);

  const handleAlterarBairro = (bairro: string) => {
    const bairroSelecionadoObj = bairrosDisponiveis.find(b => b.name === bairro);
    if (bairroSelecionadoObj) {
      setBairroSelecionado(bairro);
      setFrete(bairroSelecionadoObj.tax);
      localStorage.setItem('freteCalculado', bairroSelecionadoObj.tax.toString());
      localStorage.setItem('bairroSelecionado', bairro);
      console.log('Frete salvo no localStorage:', bairroSelecionadoObj.tax);
      console.log('Bairro salvo no localStorage:', bairro);
    }
  };

  const adicionarProduto = async (productId: number) => {
  try {
    await api.post('/cart/add', null, { params: { productId } });
    await carregarCarrinho();
    window.dispatchEvent(new CustomEvent('cartUpdated'));
    setError('');
  } catch (err: any) {
    console.error('Erro ao adicionar produto:', err);
    
    // Busca a mensagem do backend na ordem de prioridade
    const errorMessage = 
      err?.response?.data?.message ||
      err?.response?.data?.error ||
      err?.response?.data ||
      err.message ||
      'Erro ao adicionar produto ao carrinho.';
    
    setError(errorMessage);
  }
};

  const diminuirQuantidade = async (productId: number) => {
    try {
      const produtoAtual = produtos.find(p => p.productId === productId);
      if (produtoAtual && produtoAtual.quantity > 1) {
        await api.post('/cart/decrease', null, { params: { productId } });
        await carregarCarrinho();
        window.dispatchEvent(new CustomEvent('cartUpdated'));
      }
    } catch(err) {
      console.error('Erro ao diminuir quantidade:', err);
    }
  };

  const removerProduto = async (productId: number) => {
    try {
      await api.delete('/cart/remove', { params: { productId } });
      await carregarCarrinho();
      window.dispatchEvent(new CustomEvent('cartUpdated'));
      setError('');
    } catch (err) {
      console.error('Erro ao remover produto:', err);
    }
  };

  const totalProdutos = produtos.reduce((soma, p) => soma + p.productPrice * p.quantity, 0);
  const total = totalProdutos + frete;

  const finalizarCompra = async () => {
    if (!bairroSelecionado) {
      alert("Por favor, selecione um bairro para calcular o frete.");
      return;
    }
    if (tipoCarrinho === 'MISTO') {
      alert("Não é possível finalizar pedidos com produtos homeopáticos e de revenda ao mesmo tempo. Por favor, finalize os pedidos separadamente.");
      return;
    }
    if (isProcessing) return;

    setIsProcessing(true);

    try {
      console.log("Enviando requisição para /orders/create-from-cart...");
      const response = await api.post('/orders/create-from-cart', {
        neighborhood: bairroSelecionado
      });
      console.log("Resposta do backend recebida:", response.data);

      if (tipoCarrinho === 'REVENDA') {
        const paymentUrl = response.data.paymentUrl;
        if (!paymentUrl || typeof paymentUrl !== 'string') {
          throw new Error("A resposta do servidor não incluiu uma URL de pagamento válida.");
        }
        console.log("Redirecionando para:", paymentUrl);
        window.location.href = paymentUrl;
      } else if (tipoCarrinho === 'HOMEOPATICO') {
        localStorage.setItem('pedidoAtual', JSON.stringify(response.data));
        navigate('/solicitar-orcamento');
      }
    } catch (error: any) {
      console.error("Erro ao finalizar o pedido:", error);
      const errorMessage = error.response?.data?.error || error.message || "Ocorreu um erro desconhecido.";
      alert(`Erro ao finalizar: ${errorMessage}`);
      setIsProcessing(false);
    }
  };

  const continuarCompra = () => {
    navigate('/pagina-de-produtos');
  };

  if (loading) {
    return <div className="pagina-carrinho">Carregando carrinho...</div>;
  }

  return (
    <div className="pagina-carrinho">
      <IndicadorProgresso passos={3} passoAtual={1} />
      <h1 className="titulo">Seu Carrinho</h1>
      {error && (
        <div className="mensagem-erro">
          {error}
        </div>
      )}
      <div className="conteudo-carrinho">
        <div className="produtos">
          {produtos.length === 0 ? (
            <p className="mensagem-vazio">Seu carrinho está vazio.</p>
          ) : (
            produtos.map((produto) => (
              <ItemProduto
                key={produto.cartItemId || produto.productId}
                id={produto.productId}
                nome={produto.productName}
                preco={produto.productPrice}
                photo={produto.productPhotoBase64 || ''}
                quantidade={produto.quantity}
                type={produto.productType}
                onQuantidadeChange={(e) => {
                  const newQuantity = Number(e.target.value);
                  if (newQuantity > produto.quantity) {
                    adicionarProduto(produto.productId);
                  } else if (newQuantity < produto.quantity) {
                    diminuirQuantidade(produto.productId);
                  }
                }}
                onRemover={() => removerProduto(produto.productId)}
              />
            ))
          )}
        </div>
        <InformacaoEntrega
          total={total}
          frete={frete}
          bairroSelecionado={bairroSelecionado}
          observacao={observacao}
          bairros={bairrosDisponiveis.map(b => b.name)}
          onAlterarBairro={handleAlterarBairro}
          onAlterarObservacao={setObservacao}
          onContinuarCompra={continuarCompra}
          onFinalizar={finalizarCompra}
        />
        {tipoCarrinho === 'MISTO' && (
          <div className="info-carrinho-misto">
            <p>
              <strong>Atenção:</strong> Seu carrinho contém produtos homeopáticos e de revenda. 
              Não é possível finalizar pedidos mistos. Por favor, remova um dos tipos de produtos 
              para prosseguir com a finalização.
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default PaginaCarrinho;