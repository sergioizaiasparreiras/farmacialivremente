import { BrowserRouter, Routes, Route } from 'react-router-dom';

// Layouts
import LandingLayout from './LandingLayout';
import LayoutAdministrador from './admin/LayoutAdministrador';

// Páginas públicas
import Registro from './Registro';
import Login from './Login';
import LandingPage from './Landingpage';
import RecuperarSenha from './RecuperarSenha';
import RedefinirSenha from './RedefinirSenha';
import MeusDados from './MeusDados';
import PaginaProdutos from './PaginaProdutos';
import MeusPedidos from './MeusPedidos'; 
import PaginaCarrinho from './PaginaCarrinho';
import SolicitarOrcamento from './SolicitarOrcamento';
import PagamentoPix from './PagamentoPix';
import PaginaConsultarInsumos from './PaginaConsultarInsumos';
import PaginaAcessoNegado from './PaginaAcessoNegado';

// Páginas administrativas
import Dashboard from './admin/Dashboard';
import CadastrarProduto from './admin/CadastrarProduto';
import AlterarProduto from './admin/AlterarProduto';
import ExcluirProduto from './admin/ExcluirProduto';
import PaginaProdutosAdmin from './admin/PaginaProdutosAdmin';
import PaginaPedidosAdmin from './admin/PaginaPedidosAdmin';
import PaginaInsumosAdmin from './admin/PaginaInsumosAdmin';
import PaginaBairrosAdmin from './admin/PaginaBairrosAdmin';
import PaginaAdicionarBairro from './admin/PaginaAdicionarBairro';
import PaginaExcluirBairro from './admin/PaginaExcluirBairro';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Rota principal aponta para a landing page */}
        <Route
          path="/"
          element={
            <LandingLayout>
              <LandingPage />
            </LandingLayout>
          }
        />
        
        {/* Movendo a rota de registro para seu próprio caminho */}
        <Route path="/registro" element={<Registro />} />
        <Route path="/login" element={<Login />} />
        <Route path="/recuperar-senha" element={<RecuperarSenha />} />
        <Route path="/redefinir-senha" element={<RedefinirSenha />} />
        <Route path="/acesso-negado" element={<PaginaAcessoNegado />} />

        {/* Área pública com NavBar + Footer */}
        <Route
          path="/landing"
          element={
            <LandingLayout>
              <LandingPage />
            </LandingLayout>
          }
        />
        <Route
          path="/meus-dados"
          element={
            <LandingLayout>
              <MeusDados />
            </LandingLayout>
          }
        />
        <Route
          path="/pagina-de-produtos"
          element={
            <LandingLayout>
              <PaginaProdutos />
            </LandingLayout>
          }
        />
        <Route
          path="/meus-pedidos"
          element={
            <LandingLayout>
              <MeusPedidos />
            </LandingLayout>
          }
        />
        <Route
          path="/carrinho-de-compras"
          element={
            <LandingLayout>
              <PaginaCarrinho />
            </LandingLayout>
          }
        />
        <Route
          path="/solicitar-orcamento"
          element={
            <LandingLayout>
              <SolicitarOrcamento />
            </LandingLayout>
          }
        />
        <Route
          path="/pagamento-pix"
          element={
            <LandingLayout>
              <PagamentoPix />
            </LandingLayout>
          }
        />
        <Route
          path="/insumos/consultar"
          element={
            <LandingLayout>
              <PaginaConsultarInsumos />
            </LandingLayout>
          }
        />

        {/* Painel do Administrador */}
        <Route path="/admin" element={<LayoutAdministrador />}>
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="produtos" element={<PaginaProdutosAdmin />} />
          <Route path="produtos/incluir" element={<CadastrarProduto />} />
          <Route path="produtos/alterar/:id" element={<AlterarProduto />} />
          <Route path="produtos/excluir/:id" element={<ExcluirProduto />} />
          <Route path="pedidos" element={<PaginaPedidosAdmin />} />
          <Route path="insumos" element={<PaginaInsumosAdmin />} />
          
          {/* Rotas para gerenciamento de bairros */}
          <Route path="bairros" element={<PaginaBairrosAdmin />} />
          <Route path="bairros/adicionar" element={<PaginaAdicionarBairro />} />
          <Route path="bairros/excluir/:id" element={<PaginaExcluirBairro />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;