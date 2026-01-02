import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { api } from '../services/api';
import '../styles/Login.css';
import logo from '../assets/Logo.png';
import homeopatia from '../assets/Homeopatia.jpg';
import { AiOutlineEye, AiOutlineEyeInvisible } from 'react-icons/ai';

/**
 * Componente de Login que gerencia a autenticação de usuários
 * @component
 * @returns {JSX.Element} Elemento JSX contendo o formulário de login
 */
export default function Login(): JSX.Element {
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [mensagemErro, setMensagemErro] = useState('');
  const [loading, setLoading] = useState(false);

  /**
   * Manipula mudanças nos campos do formulário
   * @param {React.ChangeEvent<HTMLInputElement>} e - Evento de mudança
   */
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  /**
   * Submete o formulário de login e autentica o usuário
   * @async
   * @returns {Promise<void>}
   */
  const handleLogin = async (): Promise<void> => {
  setMensagemErro('');
  setLoading(true);

  try {
    const response = await api.post('/auth/login', form);
    
    console.log('ROLE RECEBIDO:', response.data.role); // <--- DEBUG

    localStorage.setItem('token', response.data.token);
    localStorage.setItem('userName', response.data.name);
    localStorage.setItem('userEmail', response.data.email);
    localStorage.setItem('userRole', response.data.role);

    const isAdmin = response.data.role === 'ADMIN' || 
                   form.email.toLowerCase() === 'admin@admin.com';

    setTimeout(() => {
      navigate(isAdmin ? '/admin/dashboard' : '/landing', { replace: true });
    }, 50);

  } catch (err: any) {
    setMensagemErro(err.response?.data || 'Usuário ou senha inválidos.');
    setLoading(false);
  }
};

  return (
    <div className="login-container">
      <div className="login-form">
        <img src={logo} alt="Logo" className="login-logo" />
        <h2>Faça login</h2>
        <p className="subtitle">
          Não tem uma conta? <Link to="/registro">Crie aqui</Link>
        </p>

        {mensagemErro && <p className="erro">{mensagemErro}</p>}

        <input
          type="email"
          name="email"
          placeholder="E-mail"
          value={form.email}
          onChange={handleChange}
          disabled={loading}
        />

        <div className="password-container">
          <input
            type={showPassword ? 'text' : 'password'}
            name="password"
            placeholder="Senha"
            value={form.password}
            onChange={handleChange}
            disabled={loading}
          />
          <button
            type="button"
            className="toggle-password"
            onClick={() => setShowPassword(!showPassword)}
            disabled={loading}
          >
            {showPassword ? (
              <AiOutlineEyeInvisible style={{ color: '#008400' }} />
            ) : (
              <AiOutlineEye style={{ color: '#008400' }} />
            )}
          </button>
        </div>

        <div className="login-link">
          Esqueceu a senha? <Link to="/recuperar-senha">Clique aqui</Link>
        </div>

        <button 
          className="primary-btn" 
          onClick={handleLogin} 
          disabled={loading}
        >
          {loading ? 'Entrando...' : 'Entrar'}
        </button>
      </div>

      <div className="login-image">
        <img src={homeopatia} alt="Homeopatia" />
      </div>
    </div>
  );
}