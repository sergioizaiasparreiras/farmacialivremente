import React, { useState, useEffect } from 'react';
import { FaSearch, FaDownload } from 'react-icons/fa';

import '../styles/PaginaConsultarInsumos.css';
import { api } from '../services/api';

interface Insumo {
  id: number;
  name: string;
  available: boolean;
}

const PaginaConsultarInsumos: React.FC = () => {
  const [insumos, setInsumos] = useState<Insumo[]>([]);
  const [insumosExibidos, setInsumosExibidos] = useState<Insumo[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [termoBusca, setTermoBusca] = useState('');
  const [filtroDisponibilidade, setFiltroDisponibilidade] = useState<boolean | null>(null);

  // Carregar insumos
  useEffect(() => {
    const carregarInsumos = async () => {
      try {
        setCarregando(true);
        setErro(null);
        
        const resposta = await api.get('/inputs');
        const dados = resposta.data || [];
        const dadosArray = Array.isArray(dados) ? dados : [dados];
        
        setInsumos(dadosArray);
        setInsumosExibidos(dadosArray);
      } catch (error) {
        console.error('Erro ao carregar insumos:', error);
        setErro('Não foi possível carregar os insumos. Tente novamente mais tarde.');
        setInsumos([]);
        setInsumosExibidos([]);
      } finally {
        setCarregando(false);
      }
    };

    carregarInsumos();
  }, []);

  // Filtrar insumos quando o termo de busca ou filtro de disponibilidade mudar
  useEffect(() => {
    let resultado = [...insumos];
    
    // Aplicar filtro de busca
    if (termoBusca.trim()) {
      resultado = resultado.filter(insumo => 
        insumo.name.toLowerCase().includes(termoBusca.toLowerCase())
      );
    }
    
    // Aplicar filtro de disponibilidade
    if (filtroDisponibilidade !== null) {
      resultado = resultado.filter(insumo => insumo.available === filtroDisponibilidade);
    }
    
    setInsumosExibidos(resultado);
  }, [insumos, termoBusca, filtroDisponibilidade]);

  const handleExportarRelatorio = () => {
    // Gera um arquivo CSV simples
    const dadosCSV = [
      ['Nome', 'Disponibilidade'],
      ...insumosExibidos.map(insumo => [
        insumo.name,
        insumo.available ? 'Disponível' : 'Indisponível'
      ])
    ].map(row => row.join(',')).join('\n');
    
    const blob = new Blob([dadosCSV], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'relatorio_insumos.csv';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  return (
    <div className="pagina-consultar-insumos">
      <h1>Consulta de Insumos Homeopáticos</h1>
      
      <div className="acoes-consulta">
        <div className="campo-busca">
          <FaSearch className="icone-busca" />
          <input
            type="text"
            placeholder="Buscar por nome..."
            value={termoBusca}
            onChange={(e) => setTermoBusca(e.target.value)}
          />
        </div>
        
        <div className="filtros">
          <div className="filtro-disponibilidade">
            <label>Disponibilidade:</label>
            <select 
              value={filtroDisponibilidade === null ? '' : filtroDisponibilidade ? 'disponivel' : 'indisponivel'} 
              onChange={(e) => {
                if (e.target.value === '') setFiltroDisponibilidade(null);
                else setFiltroDisponibilidade(e.target.value === 'disponivel');
              }}
            >
              <option value="">Todos</option>
              <option value="disponivel">Disponíveis</option>
              <option value="indisponivel">Indisponíveis</option>
            </select>
          </div>
          
          <button className="btn-exportar" onClick={handleExportarRelatorio}>
            <FaDownload /> Exportar Relatório
          </button>
        </div>
      </div>

      {erro && (
        <div className="mensagem-erro">
          {erro}
        </div>
      )}

      {carregando ? (
        <div className="carregando">Carregando insumos...</div>
      ) : (
        <div className="tabela-insumos">
          {insumosExibidos.length > 0 ? (
            <table>
              <thead>
                <tr>
                  <th>Nome</th>
                  <th>Disponibilidade</th>
                </tr>
              </thead>
              <tbody>
                {insumosExibidos.map(insumo => (
                  <tr key={insumo.id} className={!insumo.available ? 'indisponivel' : ''}>
                    <td>{insumo.name}</td>
                    <td className={`status ${insumo.available ? 'disponivel' : 'indisponivel'}`}>
                      {insumo.available ? 'Disponível' : 'Indisponível'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <div className="sem-resultados">
              Nenhum insumo encontrado com os filtros aplicados.
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default PaginaConsultarInsumos;