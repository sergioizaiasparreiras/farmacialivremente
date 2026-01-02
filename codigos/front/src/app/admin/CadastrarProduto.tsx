import React, { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../../services/api';
import '../../styles/Produto.css'; 

const categoriasDisponiveis = ['CREMES', 'ARNICA', 'GEIS', 'POMADAS'] as const;
type Categoria = typeof categoriasDisponiveis[number];

const CadastrarProduto: React.FC = () => {
  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [preco, setPreco] = useState('');
  const [disponivel, setDisponivel] = useState(true);
  const [tipo, setTipo] = useState<'REVENDA' | 'HOMEOPATICO'>('REVENDA');
  const [categoriasSelecionadas, setCategoriasSelecionadas] = useState<Categoria[]>([]);
  const [imagem, setImagem] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);
  const [mensagem, setMensagem] = useState('');
  const [mensagemTipo, setMensagemTipo] = useState<'sucesso' | 'erro' | ''>('');

  const inputFileRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();

  const handleCategoriaChange = (cat: Categoria) => {
    setCategoriasSelecionadas(prev =>
      prev.includes(cat) ? prev.filter(c => c !== cat) : [...prev, cat]
    );
  };

  const handleImagemChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setImagem(file);
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => setPreview(reader.result as string);
      reader.readAsDataURL(file);
    } else {
      setPreview(null);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!nome || !descricao || isNaN(Number(preco)) || Number(preco) <= 0 || !imagem || categoriasSelecionadas.length === 0) {
      setMensagem('Preencha todos os campos obrigatórios corretamente.');
      setMensagemTipo('erro');
      return;
    }

    const formData = new FormData();
    formData.append('nome', nome);
    formData.append('descricao', descricao);
    formData.append('preco', preco);
    formData.append('disponivel', String(disponivel));
    formData.append('tipo', tipo);
    categoriasSelecionadas.forEach(cat => formData.append('categorias', cat));
    formData.append('imagem', imagem);

    try {
      const response = await api.post('/product', formData);
      if (response.status === 201 || response.status === 200) {
        setMensagem('Produto cadastrado com sucesso!');
        setMensagemTipo('sucesso');
        setTimeout(() => navigate('/admin/produtos'), 1000);
      } else {
        throw new Error('Erro inesperado ao cadastrar produto.');
      }
    } catch (error: any) {
      console.error(error);
      const msg = error?.response?.data?.message || 'Erro interno no servidor.';
      setMensagem(msg);
      setMensagemTipo('erro');
    }
  };

  const handleCancelar = () => {
    navigate('/admin/produtos');
  };

  return (
    <div className="cadastrar-produto-container">
      <h2 className="cadastrar-produto-title">Cadastrar Produto</h2>
      <form className="produto-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Nome</label>
          <input type="text" value={nome} onChange={e => setNome(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>Descrição</label>
          <textarea value={descricao} onChange={e => setDescricao(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>Preço</label>
          <input type="number" step="0.01" value={preco} onChange={e => setPreco(e.target.value)} required />
        </div>
        <div className="form-group-inline">
          <p>Disponível?</p>
          <div className="radio-group">
            <button
              type="button"
              className={`radio-button ${disponivel ? 'active' : ''}`}
              onClick={() => setDisponivel(true)}
            >
              Sim
            </button>
            <button
              type="button"
              className={`radio-button ${!disponivel ? 'active' : ''}`}
              onClick={() => setDisponivel(false)}
            >
              Não
            </button>
          </div>
        </div>
        <div className="form-group-inline">
          <p>Tipo do Produto</p>
          <div className="radio-group">
            <button
              type="button"
              className={`radio-button ${tipo === 'REVENDA' ? 'active' : ''}`}
              onClick={() => setTipo('REVENDA')}
            >
              REVENDA
            </button>
            <button
              type="button"
              className={`radio-button ${tipo === 'HOMEOPATICO' ? 'active' : ''}`}
              onClick={() => setTipo('HOMEOPATICO')}
            >
              HOMEOPÁTICO
            </button>
          </div>
        </div>
        <div className="form-group-inline">
          <p>Categorias</p>
          <div className="categorias-container">
            {categoriasDisponiveis.map((cat) => (
              <button
                key={cat}
                type="button"
                className={`categoria-button ${categoriasSelecionadas.includes(cat) ? 'active' : ''}`}
                onClick={() => handleCategoriaChange(cat)}
              >
                {cat}
              </button>
            ))}
          </div>
        </div>
        <div className="form-group">
          <label htmlFor="imagem" className="btn-upload">
            Anexe a imagem do produto aqui
            <input
              type="file"
              id="imagem"
              accept="image/*"
              style={{ display: 'none' }}
              ref={inputFileRef}
              onChange={handleImagemChange}
              required
            />
          </label>
          {preview && <img src={preview} alt="Pré-visualização" className="preview-imagem" />}
        </div>
        <div className="form-actions">
          <button type="button" className="btn-cancelar" onClick={handleCancelar}>
            Cancelar
          </button>
          <button type="submit" className="btn-salvar">
            Cadastrar Produto
          </button>
        </div>
        {mensagem && <div className={`mensagem ${mensagemTipo}`}>{mensagem}</div>}
      </form>
    </div>
  );
};

export default CadastrarProduto;