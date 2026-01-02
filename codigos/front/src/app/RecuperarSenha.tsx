import { useState } from 'react';
import { api } from '../services/api';
import '../styles/Login.css';
import logo from '../assets/Logo.png';
import homeopatia from '../assets/Homeopatia.jpg';

export default function RecuperarSenha() {
  const [email, setEmail] = useState('');
  const [mensagemErro, setMensagemErro] = useState('');
  const [mensagemSucesso, setMensagemSucesso] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    setMensagemErro('');
    setMensagemSucesso('');

    try {
      setLoading(true);
      await api.post('/auth/forgot-password', { email });
      setMensagemSucesso('Um link de recuperação foi enviado para o seu e-mail.');
    } catch (err: any) {
      if (err.response?.data && typeof err.response.data === 'string') {
        setMensagemErro(err.response.data);
      } else {
        setMensagemErro('Erro ao enviar o link de recuperação. Verifique o e-mail informado.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-form">
        <img src={logo} alt="Logo" className="login-logo" />
        <h2 className="recuperar-senha-title">Recupere sua Senha</h2>

        {mensagemErro && <p className="erro">{mensagemErro}</p>}
        {mensagemSucesso && <p className="sucesso">{mensagemSucesso}</p>}

        <input
          type="email"
          name="email"
          placeholder="E-mail"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <button className="primary-btn" onClick={handleSubmit} disabled={loading}>
          {loading ? 'Enviando...' : 'Enviar link de recuperação'}
        </button>

        <p className="login-link recuperar-senha-link">
          Lembrou sua senha? <a href="/login">Voltar ao login</a>
        </p>
      </div>

      <div className="login-image">
        <img src={homeopatia} alt="Homeopatia" />
      </div>
    </div>
  );
}