import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { IMaskInput } from 'react-imask';
import '../styles/Registro.css';
import logo from '../assets/Logo.png';
import homeopatia from '../assets/Homeopatia.jpg';
import { AiOutlineEye, AiOutlineEyeInvisible } from 'react-icons/ai';

type TipoUsuario = 'cliente' | 'medico';

// Função de validação de CRM atualizada (só números)
function validarCRM(crm: string) {
  if (!crm || crm.trim() === '') return false;
  return /^\d{4,6}$/.test(crm); // Apenas 4 a 6 dígitos
}

export default function Registro() {
  const navigate = useNavigate();
  const [tipo, setTipo] = useState<TipoUsuario>('cliente');
  const [mensagemErro, setMensagemErro] = useState('');
  const [mensagemSucesso, setMensagemSucesso] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [confirmPassword, setConfirmPassword] = useState('');
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: '',
    crm: '',
  });
  const [crmDisponivel, setCrmDisponivel] = useState(true);
  const [verificandoCrm, setVerificandoCrm] = useState(false);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    // Se for o campo CRM, aceita apenas números
    if (name === 'crm') {
      const numericValue = value.replace(/\D/g, ''); // Remove tudo que não é dígito
      setForm({ ...form, [name]: numericValue });
    } else {
      setForm({ ...form, [name]: value });
    }
  };

  const handleMaskedChange = (name: string, value: string) => {
    setForm({ ...form, [name]: value || '' });
  };

  // Função para verificar se CRM é válido via API
  const checarCrm = async (crm: string) => {
    if (!crm || !validarCRM(crm)) {
      setCrmDisponivel(true);
      return;
    }

    setVerificandoCrm(true);
    try {
      const response = await api.get(`/auth/check-crm?crm=${encodeURIComponent(crm)}`);
      setCrmDisponivel(response.data); // Se a API retorna true, o CRM é válido
    } catch (error) {
      console.error('Erro ao verificar CRM:', error);
      setCrmDisponivel(false); // Em caso de erro, considera inválido
    } finally {
      setVerificandoCrm(false);
    }
  };

  const handleSubmit = async () => {
    setMensagemErro('');
    setMensagemSucesso('');

    if (form.password !== confirmPassword) {
      setMensagemErro('As senhas não coincidem.');
      return;
    }

    if (tipo === 'medico') {
      if (!form.crm || form.crm.trim() === '') {
        setMensagemErro('CRM é obrigatório para médicos.');
        return;
      }

      if (!validarCRM(form.crm)) {
        setMensagemErro('CRM inválido. Digite apenas números (4 a 6 dígitos).');
        return;
      }

      if (!crmDisponivel) {
        setMensagemErro('Este CRM não é válido ou não foi encontrado.');
        return;
      }
    }

    // Limpar o telefone removendo caracteres não numéricos
    const cleanPhone = (form.phone || '').replace(/\D/g, '');

    const payload =
      tipo === 'cliente'
        ? {
          fullName: form.fullName,
          email: form.email,
          password: form.password,
          phone: cleanPhone,
        }
        : {
          fullName: form.fullName,
          email: form.email,
          password: form.password,
          phone: cleanPhone,
          crm: form.crm,
        };

    const endpoint = tipo === 'cliente' ? '/auth/register/client' : '/auth/register/doctor';

    try {
      const response = await api.post(endpoint, payload);
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('userName', response.data.name);
      setMensagemSucesso('Cadastro realizado com sucesso!');
      setTimeout(() => navigate('/login'), 2000);
    } catch (error: any) {
      if (error.response && error.response.data) {
        const data = error.response.data;

        if (typeof data === 'string') {
          setMensagemErro(data);
        } else if (typeof data === 'object') {
          const mensagem = Object.values(data).join('\n');
          setMensagemErro(mensagem);
        } else {
          setMensagemErro('Erro inesperado ao cadastrar usuário.');
        }
      } else {
        setMensagemErro('Erro ao conectar com o servidor.');
      }
    }
  };

  return (
    <div className="registro-container">
      <div className="registro-form">
        <div className="registro-header">
          <img src={logo} alt="Logo" className="registro-logo" />
          <h2>Crie sua conta</h2>
        </div>

        <div className="switch-user">
          <button onClick={() => setTipo('cliente')} className={tipo === 'cliente' ? 'active' : ''}>
            Sou Cliente
          </button>
          <button onClick={() => setTipo('medico')} className={tipo === 'medico' ? 'active' : ''}>
            Sou Médico
          </button>
        </div>

        <div className={`form-content ${tipo}`}>
          <input
            type="text"
            name="fullName"
            placeholder="Nome"
            value={form.fullName}
            onChange={handleInputChange}
          />
          <input
            type="email"
            name="email"
            placeholder="E-mail"
            value={form.email}
            onChange={handleInputChange}
          />

          <IMaskInput
            mask="(00) 00000-0000"
            value={form.phone || ''}
            name="phone"
            onAccept={(value: any) => handleMaskedChange('phone', value || '')}
            placeholder="Telefone"
          />

          {tipo === 'medico' && (
            <div>
              <input
                type="text"
                name="crm"
                placeholder="CRM (apenas números, ex: 12345)"
                value={form.crm || ''}
                onChange={handleInputChange}
                onBlur={(e) => {
                  checarCrm(e.target.value);
                }}
                maxLength={6}
                autoComplete="off"
              />
              {verificandoCrm && (
                <span style={{ color: '#666', fontSize: '12px' }}>Verificando CRM...</span>
              )}
              {!crmDisponivel && form.crm && validarCRM(form.crm) && (
                <span className="erro">Este CRM não é válido ou não foi encontrado.</span>
              )}
              {form.crm && !validarCRM(form.crm) && (
                <span className="erro">Digite apenas números (4 a 6 dígitos).</span>
              )}
            </div>
          )}

          <div className="password-container">
            <input
              type={showPassword ? 'text' : 'password'}
              name="password"
              placeholder="Senha"
              value={form.password}
              onChange={handleInputChange}
            />
            <button
              type="button"
              className="toggle-password"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? (
                <AiOutlineEyeInvisible style={{ color: '#008400' }} />
              ) : (
                <AiOutlineEye style={{ color: '#008400' }} />
              )}
            </button>
          </div>

          <input
            type={showPassword ? 'text' : 'password'}
            name="confirmPassword"
            placeholder="Confirmar senha"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />

          {mensagemErro && <p className="erro">{mensagemErro}</p>}
          {mensagemSucesso && <p className="sucesso">{mensagemSucesso}</p>}

          <button className="primary-btn" onClick={handleSubmit}>
            Cadastrar
          </button>

          <p className="login-link">
            Já tem uma conta? <a href="/login">Fazer login</a>
          </p>
        </div>
      </div>

      <div className="registro-image">
        <img src={homeopatia} alt="Homeopatia" />
      </div>
    </div>
  );
}