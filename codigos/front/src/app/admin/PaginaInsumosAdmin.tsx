import React, { useState, useEffect } from 'react';
import ItemInsumo from '../admin/ItemInsumoAdmin';
import '../../styles/PaginaInsumosAdmin.css';
import { api } from '../../services/api';

interface Insumo {
  id: number;
  name: string;
  available: boolean;
}

const PainelInsumos = () => {
  const [insumos, setInsumos] = useState<Insumo[]>([]);
  const [insumosExibidos, setInsumosExibidos] = useState<Insumo[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [termoBusca, setTermoBusca] = useState('');
  const [filtroDisponibilidade, setFiltroDisponibilidade] = useState<string>('todos');
  const [ordenacao, setOrdenacao] = useState<string>('alfabetica');
  const [paginaAtual, setPaginaAtual] = useState(1);
  const [itensPorPagina] = useState(20);

  const carregarInsumos = async () => {
    try {
      setCarregando(true);
      setErro(null);
      
      const resposta = await api.get('/inputs');
      
      // Verifica se a resposta tem dados e converte para array se necessário
      const dados = resposta.data || [];
      const dadosArray = Array.isArray(dados) ? dados : [dados];
      
      setInsumos(dadosArray);
      setInsumosExibidos(dadosArray);
    } catch (error) {
      console.error('Erro ao carregar insumos:', error);
      setErro('Não foi possível carregar os insumos. Tente novamente mais tarde.');
      setInsumos([]); // Garante que insumos seja um array vazio
      setInsumosExibidos([]);
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    carregarInsumos();
  }, []);

  // Aplicar filtros e ordenação sempre que os critérios mudarem
  useEffect(() => {
    let resultado = [...insumos];

    // Aplicar filtro de busca
    if (termoBusca.trim()) {
      resultado = resultado.filter(insumo => 
        insumo.name.toLowerCase().includes(termoBusca.toLowerCase())
      );
    }

    // Aplicar filtro de disponibilidade
    if (filtroDisponibilidade === 'disponiveis') {
      resultado = resultado.filter(insumo => insumo.available === true);
    } else if (filtroDisponibilidade === 'indisponiveis') {
      resultado = resultado.filter(insumo => insumo.available === false);
    }

    // Aplicar ordenação
    if (ordenacao === 'alfabetica') {
      resultado.sort((a, b) => a.name.localeCompare(b.name, 'pt-BR'));
    } else if (ordenacao === 'alfabetica-desc') {
      resultado.sort((a, b) => b.name.localeCompare(a.name, 'pt-BR'));
    }

    setInsumosExibidos(resultado);
    setPaginaAtual(1); // Reset para primeira página quando filtros mudam
  }, [insumos, termoBusca, filtroDisponibilidade, ordenacao]);

  // Calcular itens da página atual
  const indiceInicio = (paginaAtual - 1) * itensPorPagina;
  const indiceFim = indiceInicio + itensPorPagina;
  const itensPaginaAtual = insumosExibidos.slice(indiceInicio, indiceFim);
  const totalPaginas = Math.ceil(insumosExibidos.length / itensPorPagina);

  const irParaPagina = (pagina: number) => {
    setPaginaAtual(pagina);
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

  const handleUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    if (!event.target.files?.length) return;
    
    const file = event.target.files[0];
    console.log('Arquivo selecionado:', file.name, 'Tamanho:', file.size, 'Tipo:', file.type);
    
    try {
      setErro(null);
      setCarregando(true);
      
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await api.post('/inputs/importar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      
      console.log('Upload bem-sucedido:', response.data);
      await carregarInsumos();
      
    } catch (error: any) {
      console.error('Erro completo ao fazer upload do arquivo:', error);
      
      let mensagemErro = 'Falha ao importar insumos.';
      
      if (error.response) {
        // Erro da resposta do servidor
        console.error('Status:', error.response.status);
        console.error('Data:', error.response.data);
        
        if (typeof error.response.data === 'string') {
          mensagemErro = error.response.data;
        } else if (error.response.data?.message) {
          mensagemErro = error.response.data.message;
        } else {
          mensagemErro = `Erro do servidor (${error.response.status}): ${JSON.stringify(error.response.data)}`;
        }
      } else if (error.request) {
        // Erro de rede
        console.error('Erro de rede:', error.request);
        mensagemErro = 'Erro de conexão com o servidor. Verifique sua internet e tente novamente.';
      } else {
        // Erro na configuração da requisição
        console.error('Erro na configuração:', error.message);
        mensagemErro = 'Erro inesperado: ' + error.message;
      }
      
      setErro(mensagemErro);
    } finally {
      setCarregando(false);
      // Limpar o input para permitir upload do mesmo arquivo novamente
      event.target.value = '';
    }
  };

  const atualizarDisponibilidade = async (id: number, novoStatus: boolean) => {
    try {
      await api.put(`/inputs/${id}/available`, null, {
        params: { available: novoStatus }
      });
      
      setInsumos(prevInsumos => 
        prevInsumos.map(insumo =>
          insumo.id === id ? { ...insumo, available: novoStatus } : insumo
        )
      );
    } catch (error) {
      console.error('Erro ao atualizar disponibilidade:', error);
      setErro('Não foi possível atualizar a disponibilidade. Tente novamente.');
    }
  };

  return (
    <div className="painel-insumos">
      <div className="cabecalho-pagina">
        <h1>Insumos</h1>
        <p className="subtitulo">Gerencie os insumos disponíveis no sistema e importe novos arquivos</p>
      </div>

      <div className="painel-importacao">
        <label htmlFor="upload-input" className="btn-primary">
          Importar novos insumos 
        </label>
        <input 
          id="upload-input" 
          type="file" 
          onChange={handleUpload} 
          accept=".xlsx,.xls" 
          hidden 
        />
      </div>

      {/* Barra de pesquisa e filtros */}
      <div className="controles-busca">
        <div className="campo-busca">
          <input
            type="text"
            placeholder="Buscar por nome do insumo..."
            value={termoBusca}
            onChange={(e) => setTermoBusca(e.target.value)}
            className="input-busca"
          />
        </div>
        
        <div className="filtros-container">
          <div className="filtro-grupo">
            <label htmlFor="filtro-disponibilidade">Disponibilidade:</label>
            <select 
              id="filtro-disponibilidade"
              value={filtroDisponibilidade} 
              onChange={(e) => setFiltroDisponibilidade(e.target.value)}
              className="select-filtro"
            >
              <option value="todos">Todos</option>
              <option value="disponiveis">Disponíveis</option>
              <option value="indisponiveis">Indisponíveis</option>
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
              <option value="alfabetica">A-Z</option>
              <option value="alfabetica-desc">Z-A</option>
            </select>
          </div>
        </div>
      </div>

      {/* Contador de resultados */}
      {!carregando && (
        <div className="contador-resultados">
          Mostrando {itensPaginaAtual.length} de {insumosExibidos.length} insumos
        </div>
      )}

      {erro && (
        <div className="mensagem-erro">
          {erro}
        </div>
      )}

      {carregando ? (
        <div className="carregando">Carregando insumos...</div>
      ) : (
        <div className="painel-lista">
          {Array.isArray(insumosExibidos) && insumosExibidos.length > 0 ? (
            itensPaginaAtual.map(insumo => (
              <ItemInsumo
                key={insumo.id}
                insumo={insumo}
                onAtualizarDisponibilidade={atualizarDisponibilidade}
              />
            ))
          ) : (
            <div className="sem-insumos">
              {termoBusca || filtroDisponibilidade !== 'todos' 
                ? 'Nenhum insumo encontrado com os filtros aplicados.' 
                : 'Nenhum insumo encontrado. Importe novos insumos para começar.'
              }
            </div>
          )}
        </div>
      )}

      {/* Paginação */}
      {!carregando && insumosExibidos.length > itensPorPagina && (
        <div className="paginacao-container">
          <div className="paginacao-info">
            Total de registros: {insumosExibidos.length}
          </div>
          <div className="paginacao-controles">
            <button 
              className="btn-paginacao" 
              onClick={paginaAnterior} 
              disabled={paginaAtual === 1}
            >
              ‹
            </button>
            
            {/* Números das páginas */}
            {Array.from({ length: Math.min(totalPaginas, 5) }, (_, i) => {
              let numeroPagina;
              if (totalPaginas <= 5) {
                numeroPagina = i + 1;
              } else if (paginaAtual <= 3) {
                numeroPagina = i + 1;
              } else if (paginaAtual >= totalPaginas - 2) {
                numeroPagina = totalPaginas - 4 + i;
              } else {
                numeroPagina = paginaAtual - 2 + i;
              }
              
              return (
                <button
                  key={numeroPagina}
                  className={`btn-numero-pagina ${paginaAtual === numeroPagina ? 'ativo' : ''}`}
                  onClick={() => irParaPagina(numeroPagina)}
                >
                  {numeroPagina}
                </button>
              );
            })}
            
            <button 
              className="btn-paginacao" 
              onClick={proximaPagina} 
              disabled={paginaAtual === totalPaginas}
            >
              ›
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default PainelInsumos;