import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { AiOutlineEye, AiOutlineEyeInvisible } from 'react-icons/ai';
import { api } from '../services/api';
import '../styles/RedefinirSenha.css';
import logo from '../assets/Logo.png';
import homeopatia from '../assets/Homeopatia.jpg';

export default function RedefinirSenha() {
  const [token, setToken] = useState('');
  const [novaSenha, setNovaSenha] = useState('');
  const [confirmarSenha, setConfirmarSenha] = useState('');
  const [mostrarSenha, setMostrarSenha] = useState(false);
  const [mensagemErro, setMensagemErro] = useState('');
  const [mensagemSucesso, setMensagemSucesso] = useState('');
  const [loading, setLoading] = useState(false);
  const [params] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const tokenURL = params.get('token');
    if (tokenURL) setToken(tokenURL);
    else setMensagemErro('Token inválido ou expirado.');
  }, [params]);

  const isSenhaForte = (senha: string) => {
    const regras = [
      /.{8,}/, // mínimo 8 caracteres
      /[A-Z]/, // pelo menos uma letra maiúscula
      /\d/, // pelo menos um número
    ];
    return regras.every((regex) => regex.test(senha));
  };

  const handleSubmit = async () => {
    setMensagemErro('');
    setMensagemSucesso('');

    if (novaSenha !== confirmarSenha) {
      setMensagemErro('As senhas não coincidem.');
      return;
    }

    if (!isSenhaForte(novaSenha)) {
      setMensagemErro(
        'A senha precisa ter ao menos 8 caracteres, uma letra maiúscula e um número.',
      );
      return;
    }

    try {
      setLoading(true);
      await api.post('/auth/reset-password', {
        token,
        newPassword: novaSenha,
      });
      setMensagemSucesso('Senha redefinida com sucesso! Redirecionando...');
      setTimeout(() => navigate('/login'), 3000);
    } catch (err: any) {
      if (err.response?.data && typeof err.response.data === 'string') {
        setMensagemErro(err.response.data);
      } else {
        setMensagemErro('Erro ao redefinir senha. Token inválido ou expirado.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-form">
        <img src={logo} alt="Logo" className="login-logo" />
        <h2>Nova Senha</h2>

        {mensagemErro && <p className="erro">{mensagemErro}</p>}
        {mensagemSucesso && <p className="sucesso">{mensagemSucesso}</p>}

        <div className="password-group">
          <input
            type={mostrarSenha ? 'text' : 'password'}
            placeholder="Nova senha"
            className={novaSenha ? (isSenhaForte(novaSenha) ? 'valid' : 'invalid') : ''}
            value={novaSenha}
            onChange={(e) => setNovaSenha(e.target.value)}
          />

          <input
            type={mostrarSenha ? 'text' : 'password'}
            placeholder="Confirmar senha"
            className={confirmarSenha ? (novaSenha === confirmarSenha ? 'valid' : 'invalid') : ''}
            value={confirmarSenha}
            onChange={(e) => setConfirmarSenha(e.target.value)}
          />

          <button
            type="button"
            className="toggle-password global"
            onClick={() => setMostrarSenha(!mostrarSenha)}
          >
            {mostrarSenha ? (
              <AiOutlineEyeInvisible style={{ color: '#008400' }} />
            ) : (
              <AiOutlineEye style={{ color: '#008400' }} />
            )}
          </button>
        </div>

        <button className="primary-btn" onClick={handleSubmit} disabled={loading || !token}>
          {loading ? 'Salvando...' : 'Redefinir Senha'}
        </button>

        <p className="login-link">
          Lembrou sua senha? <a href="/login">Voltar ao login</a>
        </p>
      </div>

      <div className="login-image">
        <img src={homeopatia} alt="Homeopatia" />
      </div>
    </div>
  );
}