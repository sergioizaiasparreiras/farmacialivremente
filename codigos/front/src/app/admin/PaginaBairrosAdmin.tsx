import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaSearch, FaPlus, FaTrash, FaEdit, FaCheckCircle, FaChevronLeft, FaChevronRight, FaTimes, FaSave } from 'react-icons/fa';
import '../../styles/PaginaBairrosAdmin.css';
import { api } from '../../services/api';

interface Bairro {
  id: number;
  name: string;
  tax: number;
}

interface BairroEdicao {
  name: string;
  tax: string;
}

const PaginaBairrosAdmin = () => {
  const [bairros, setBairros] = useState<Bairro[]>([]);
  const [bairrosFiltrados, setBairrosFiltrados] = useState<Bairro[]>([]);
  const [termoPesquisa, setTermoPesquisa] = useState('');
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [mensagemSucesso, setMensagemSucesso] = useState<string | null>(null);
  
  // Estados para edição
  const [modalEdicaoAberto, setModalEdicaoAberto] = useState(false);
  const [bairroEditando, setBairroEditando] = useState<Bairro | null>(null);
  const [dadosEdicao, setDadosEdicao] = useState<BairroEdicao>({ name: '', tax: '' });
  const [salvandoEdicao, setSalvandoEdicao] = useState(false);
  
  // Estados para paginação
  const [paginaAtual, setPaginaAtual] = useState(1);
  const itensPorPagina = 20;
  
  const navigate = useNavigate();

  // Dados simulados já ordenados
  const dadosSimulados: Bairro[] = [
    { id: 1, name: 'Buritis', tax: 10.00 },
    { id: 2, name: 'Centro', tax: 5.00 },
    { id: 3, name: 'Funcionários', tax: 7.50 },
    { id: 4, name: 'Lourdes', tax: 8.00 },
    { id: 5, name: 'Savassi', tax: 7.50 },
    { id: 6, name: 'Aarão Reis', tax: 12.00 },
    { id: 7, name: 'Abílio Machado', tax: 15.00 },
    { id: 8, name: 'Acaba Mundo', tax: 18.00 },
    { id: 9, name: 'Ademar Maldonado', tax: 14.00 },
    { id: 10, name: 'Aeroporto (Pampulha)', tax: 19.00 },
    { id: 11, name: 'Água Branca', tax: 20.00 },
    { id: 12, name: 'Alípio de Melo', tax: 18.00 },
    { id: 13, name: 'Alphaville', tax: 58.00 },
    { id: 14, name: 'Alto Caiçara', tax: 16.00 },
    { id: 15, name: 'Alto Santa Lúcia', tax: 15.00 },
    { id: 16, name: 'Alto Vera Cruz', tax: 15.00 },
    { id: 17, name: 'Alvaro Camargos', tax: 13.00 },
    { id: 18, name: 'Ambrosina', tax: 11.00 },
    { id: 19, name: 'América', tax: 9.00 },
    { id: 20, name: 'Anchieta', tax: 8.50 },
    { id: 21, name: 'Andiroba', tax: 17.00 },
    { id: 22, name: 'Antônio Carlos', tax: 12.50 },
    { id: 23, name: 'Aparecida', tax: 7.00 },
    { id: 24, name: 'Aparecida Sétima Seção', tax: 7.50 },
    { id: 25, name: 'Apolônia', tax: 14.50 }
  ].sort((a, b) => a.name.localeCompare(b.name));

  const carregarBairros = async () => {
    try {
      setCarregando(true);
      setErro(null);
      
      const resposta = await api.get('/neighborhoods');
      const bairrosOrdenados = Array.isArray(resposta.data) 
        ? resposta.data.sort((a, b) => a.name.localeCompare(b.name))
        : dadosSimulados;
      
      setBairros(bairrosOrdenados);
      setBairrosFiltrados(bairrosOrdenados);
    } catch (error) {
      console.error('Erro ao carregar bairros:', error);
      setErro('Não foi possível carregar os bairros. Tente novamente mais tarde.');
      setBairros(dadosSimulados);
      setBairrosFiltrados(dadosSimulados);
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    carregarBairros();
  }, []);

  // Filtrar bairros com base no termo de pesquisa
  useEffect(() => {
    if (termoPesquisa.trim() === '') {
      setBairrosFiltrados(bairros);
    } else {
      const filtrados = bairros.filter(bairro =>
        bairro.name.toLowerCase().includes(termoPesquisa.toLowerCase())
      );
      setBairrosFiltrados(filtrados);
    }
    // Resetar para primeira página quando filtrar
    setPaginaAtual(1);
  }, [termoPesquisa, bairros]);

  // Auto-remover mensagem de sucesso após 5 segundos
  useEffect(() => {
    if (mensagemSucesso) {
      const timer = setTimeout(() => {
        setMensagemSucesso(null);
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [mensagemSucesso]);

  // Cálculos da paginação
  const totalItens = bairrosFiltrados.length;
  const totalPaginas = Math.ceil(totalItens / itensPorPagina);
  const indiceInicio = (paginaAtual - 1) * itensPorPagina;
  const indiceFim = indiceInicio + itensPorPagina;
  const bairrosPaginados = bairrosFiltrados.slice(indiceInicio, indiceFim);

  const handleExcluir = async (id: number, nomeBairro: string) => {
    if (!window.confirm(`Tem certeza que deseja excluir o bairro "${nomeBairro}"?`)) {
      return;
    }

    try {
      await api.delete(`/neighborhoods/${id}`);
      setBairros(prevBairros => {
        const novosBairros = prevBairros.filter(bairro => bairro.id !== id);
        return novosBairros.sort((a, b) => a.name.localeCompare(b.name));
      });
      setMensagemSucesso(`Bairro "${nomeBairro}" excluído com sucesso!`);
      
      // Ajustar página se necessário
      const novoTotal = bairrosFiltrados.length - 1;
      const novasTotalPaginas = Math.ceil(novoTotal / itensPorPagina);
      if (paginaAtual > novasTotalPaginas && novasTotalPaginas > 0) {
        setPaginaAtual(novasTotalPaginas);
      }
    } catch (error) {
      console.error('Erro ao excluir bairro:', error);
      setErro('Não foi possível excluir o bairro. Tente novamente.');
    }
  };

  const navegarParaAdicionarBairro = () => {
    navigate('/admin/bairros/adicionar');
  };

  const limparPesquisa = () => {
    setTermoPesquisa('');
  };

  // Funções para edição
  const abrirModalEdicao = (bairro: Bairro) => {
    setBairroEditando(bairro);
    setDadosEdicao({
      name: bairro.name,
      tax: bairro.tax.toFixed(2)
    });
    setModalEdicaoAberto(true);
  };

  const fecharModalEdicao = () => {
    setModalEdicaoAberto(false);
    setBairroEditando(null);
    setDadosEdicao({ name: '', tax: '' });
    setSalvandoEdicao(false);
  };

  const handleMudancaDados = (campo: keyof BairroEdicao, valor: string) => {
    setDadosEdicao(prev => ({
      ...prev,
      [campo]: valor
    }));
  };

  const salvarEdicao = async () => {
    if (!bairroEditando) return;

    // Validações
    if (!dadosEdicao.name.trim()) {
      alert('O nome do bairro é obrigatório.');
      return;
    }

    const taxaNumero = parseFloat(dadosEdicao.tax.replace(',', '.'));
    if (isNaN(taxaNumero) || taxaNumero < 0) {
      alert('A taxa deve ser um número válido e não negativo.');
      return;
    }

    try {
      setSalvandoEdicao(true);
      
      const dadosAtualizacao = {
        name: dadosEdicao.name.trim(),
        tax: taxaNumero
      };

      await api.put(`/neighborhoods/${bairroEditando.id}`, dadosAtualizacao);
      
      // Atualizar a lista local
      setBairros(prevBairros => {
        const bairrosAtualizados = prevBairros.map(bairro =>
          bairro.id === bairroEditando.id
            ? { ...bairro, name: dadosAtualizacao.name, tax: dadosAtualizacao.tax }
            : bairro
        );
        return bairrosAtualizados.sort((a, b) => a.name.localeCompare(b.name));
      });

      setMensagemSucesso(`Bairro "${dadosAtualizacao.name}" editado com sucesso!`);
      fecharModalEdicao();
    } catch (error) {
      console.error('Erro ao salvar edição:', error);
      setErro('Não foi possível salvar as alterações. Tente novamente.');
      setSalvandoEdicao(false);
    }
  };

  const handleKeyPressModal = (e: React.KeyboardEvent) => {
    if (e.key === 'Escape') {
      fecharModalEdicao();
    } else if (e.key === 'Enter' && !salvandoEdicao) {
      salvarEdicao();
    }
  };

  // Funções de paginação
  const irParaPagina = (numeroPagina: number) => {
    setPaginaAtual(numeroPagina);
  };

  const paginaAnterior = () => {
    if (paginaAtual > 1) {
      setPaginaAtual(paginaAtual - 1);
    }
  };

  const proximaPagina = () => {
    if (paginaAtual < totalPaginas) {
      setPaginaAtual(paginaAtual + 1);
    }
  };

  // Gerar números das páginas para exibir
  const gerarNumerosPaginas = () => {
    const numeros = [];
    const maxVisivel = 5;
    let inicio = Math.max(1, paginaAtual - Math.floor(maxVisivel / 2));
    let fim = Math.min(totalPaginas, inicio + maxVisivel - 1);
    
    if (fim - inicio + 1 < maxVisivel) {
      inicio = Math.max(1, fim - maxVisivel + 1);
    }
    
    for (let i = inicio; i <= fim; i++) {
      numeros.push(i);
    }
    
    return numeros;
  };

  return (
    <div className="painel-bairros">
      <div className="cabecalho-pagina">
        <h1>Bairros e Taxas de Entrega</h1>
        <p className="subtitulo">Gerencie os bairros atendidos e suas respectivas taxas de entrega</p>
      </div>
      
      <div className="acoes-principais">
        <div className="pesquisa-container">
          <div className="campo-pesquisa">
            <FaSearch className="icone-pesquisa" />
            <input
              type="text"
              placeholder="Pesquisar bairro..."
              value={termoPesquisa}
              onChange={(e) => setTermoPesquisa(e.target.value)}
              className="input-pesquisa"
            />
            {termoPesquisa && (
              <button 
                onClick={limparPesquisa}
                className="btn-limpar-pesquisa"
                type="button"
              >
                ×
              </button>
            )}
          </div>
        </div>
        
        <button className="btn-primary" onClick={navegarParaAdicionarBairro}>
          <FaPlus className="icone-btn" />
          Adicionar Bairro
        </button>
      </div>

      {mensagemSucesso && (
        <div className="mensagem-sucesso">
          <FaCheckCircle />
          {mensagemSucesso}
        </div>
      )}

      {erro && <div className="mensagem-erro">{erro}</div>}

      {carregando ? (
        <div className="carregando">
          <div className="spinner"></div>
          Carregando bairros...
        </div>
      ) : (
        <div className="conteudo-tabela">
          {bairrosFiltrados.length > 0 ? (
            <>
              <div className="info-resultados">
                <div>
                  {termoPesquisa ? (
                    <span>
                      {bairrosFiltrados.length} resultado(s) encontrado(s) para "{termoPesquisa}"
                    </span>
                  ) : (
                    <span>{bairrosFiltrados.length} bairro(s) cadastrado(s)</span>
                  )}
                </div>
                
                {totalItens > itensPorPagina && (
                  <div className="info-paginacao">
                    Mostrando {indiceInicio + 1} a {Math.min(indiceFim, totalItens)} de {totalItens} resultados
                  </div>
                )}
              </div>
              
              <div className="tabela-container">
                <table className="tabela-bairros">
                  <thead>
                    <tr>
                      <th className="coluna-nome">Bairro</th>
                      <th className="coluna-taxa">Taxa de Entrega</th>
                      <th className="coluna-acoes">Ações</th>
                    </tr>
                  </thead>
                  <tbody>
                    {bairrosPaginados.map(bairro => (
                      <tr key={bairro.id} className="linha-bairro">
                        <td>
                          <span className="nome-bairro">{bairro.name}</span>
                        </td>
                        <td>
                          <span className="valor-taxa">R$ {bairro.tax.toFixed(2).replace('.', ',')}</span>
                        </td>
                        <td>
                          <div className="acoes-grupo">
                            <button 
                              className="btn-editar"
                              onClick={() => abrirModalEdicao(bairro)}
                              title={`Editar bairro ${bairro.name}`}
                            >
                              <FaEdit />
                            </button>
                            <button 
                              className="btn-excluir"
                              onClick={() => handleExcluir(bairro.id, bairro.name)}
                              title={`Excluir bairro ${bairro.name}`}
                            >
                              <FaTrash />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Paginação */}
              {totalPaginas > 1 && (
                <div className="paginacao-container">
                  <button 
                    className="paginacao-btn"
                    onClick={paginaAnterior}
                    disabled={paginaAtual === 1}
                    title="Página anterior"
                  >
                    <FaChevronLeft />
                  </button>

                  {gerarNumerosPaginas().map(numero => (
                    <button
                      key={numero}
                      className={`paginacao-btn ${numero === paginaAtual ? 'ativo' : ''}`}
                      onClick={() => irParaPagina(numero)}
                    >
                      {numero}
                    </button>
                  ))}

                  <button 
                    className="paginacao-btn"
                    onClick={proximaPagina}
                    disabled={paginaAtual === totalPaginas}
                    title="Próxima página"
                  >
                    <FaChevronRight />
                  </button>

                  <div className="paginacao-info">
                    Página {paginaAtual} de {totalPaginas}
                  </div>
                </div>
              )}
            </>
          ) : (
            <div className="sem-resultados">
              {termoPesquisa ? (
                <>
                  <p>Nenhum bairro encontrado para "{termoPesquisa}"</p>
                  <button onClick={limparPesquisa} className="btn-limpar">
                    Limpar pesquisa
                  </button>
                </>
              ) : (
                <>
                  <p>Nenhum bairro cadastrado</p>
                  <p>Adicione um novo bairro para começar</p>
                </>
              )}
            </div>
          )}
        </div>
      )}

      {/* Modal de Edição */}
      {modalEdicaoAberto && bairroEditando && (
        <div className="modal-overlay" onClick={fecharModalEdicao}>
          <div 
            className="modal-conteudo" 
            onClick={(e) => e.stopPropagation()}
            onKeyDown={handleKeyPressModal}
            tabIndex={-1}
          >
            <h2 className="modal-titulo">Editar Bairro</h2>
            
            <form className="formulario-edicao" onSubmit={(e) => e.preventDefault()}>
              <div className="campo-formulario">
                <label htmlFor="nome-bairro">Nome do Bairro</label>
                <input
                  id="nome-bairro"
                  type="text"
                  value={dadosEdicao.name}
                  onChange={(e) => handleMudancaDados('name', e.target.value)}
                  placeholder="Digite o nome do bairro..."
                  disabled={salvandoEdicao}
                />
              </div>

              <div className="campo-formulario">
                <label htmlFor="taxa-entrega">Taxa de Entrega (R$)</label>
                <input
                  id="taxa-entrega"
                  type="number"
                  step="0.01"
                  min="0"
                  value={dadosEdicao.tax}
                  onChange={(e) => handleMudancaDados('tax', e.target.value)}
                  placeholder="0,00"
                  disabled={salvandoEdicao}
                />
              </div>

              <div className="acoes-modal">
                <button 
                  type="button"
                  className="btn-cancelar"
                  onClick={fecharModalEdicao}
                  disabled={salvandoEdicao}
                >
                  <FaTimes />
                  Cancelar
                </button>
                <button 
                  type="button"
                  className="btn-primary"
                  onClick={salvarEdicao}
                  disabled={salvandoEdicao}
                >
                  <FaSave />
                  {salvandoEdicao ? 'Salvando...' : 'Salvar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default PaginaBairrosAdmin;