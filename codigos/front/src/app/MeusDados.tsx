import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { IMaskInput } from 'react-imask';
import { AiOutlineEye, AiOutlineEyeInvisible } from 'react-icons/ai';
import { api } from '../services/api';
import '../styles/MeusDados.css';
import Logo from '../assets/Logo.png';

const MeusDados: React.FC = () => {
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: '',
  });

  const [status, setStatus] = useState<null | 'sucesso' | 'erro'>(null);
  const [mensagem, setMensagem] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [userRole, setUserRole] = useState<'CLIENTE' | 'MEDICO' | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }

    api
      .get('/user/profile', {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        const { fullName, email, phone, role } = res.data;
        const cleanRole = role?.replace('ROLE_', '') as 'CLIENTE' | 'MEDICO';
        setForm({ fullName, email, phone, password: '' });
        setUserRole(cleanRole);
      })
      .catch(() => navigate('/login'));
  }, [navigate]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleMaskedChange = (name: string, value: string) => {
    setForm({ ...form, [name]: value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensagem('');
    setStatus(null);
    setLoading(true);

    const token = localStorage.getItem('token');
    if (!token || !userRole) {
      navigate('/login');
      return;
    }

    const endpoint = userRole === 'CLIENTE' ? '/user/profile/client' : '/user/profile/doctor';

    // Limpar o telefone removendo caracteres não numéricos (parênteses, hífens, espaços)
    const cleanPhone = form.phone.replace(/\D/g, '');

    const payload: any = {
      fullName: form.fullName,
      email: form.email,
      phone: cleanPhone, // Enviar apenas números: 31991192931
    };

    if (form.password && form.password.length >= 6) {
      payload.password = form.password;
    }

    // Logs para debug (simplificados)
    console.log('=== DEBUG ATUALIZAÇÃO DADOS ===');
    console.log('Telefone original:', form.phone);
    console.log('Telefone limpo:', cleanPhone);
    console.log('Payload enviado:', payload);

    try {
      const response = await api.put(endpoint, payload, {
        headers: { Authorization: `Bearer ${token}` },
      });
      
      console.log('✅ Dados atualizados com sucesso');
      
      // Atualizar o localStorage com o novo nome
      localStorage.setItem('userName', form.fullName);
      console.log('📝 Nome atualizado no localStorage:', form.fullName);
      
      // Disparar evento customizado para atualizar a navbar
      const event = new CustomEvent('userDataUpdated', {
        detail: { fullName: form.fullName }
      });
      window.dispatchEvent(event);
      console.log('📢 Evento userDataUpdated disparado com nome:', form.fullName);
      
      // Forçar atualização após pequeno delay como fallback
      setTimeout(() => {
        localStorage.setItem('userName', form.fullName);
        window.dispatchEvent(new CustomEvent('userDataUpdated', {
          detail: { fullName: form.fullName }
        }));
        console.log('🔄 Segundo disparo do evento após delay');
      }, 100);
      
      setMensagem('✔️ Dados atualizados com sucesso!');
      setStatus('sucesso');
      setForm({ ...form, password: '' });

      setTimeout(() => {
        navigate(-1);
      }, 2000); // redireciona após 2s
    } catch (error: any) {
      console.error('❌ Erro ao atualizar dados:', error?.response?.data || error?.message);
      
      let errorMsg = 'Erro ao atualizar os dados.';
      
      if (error?.response?.data) {
        if (typeof error.response.data === 'string') {
          errorMsg = error.response.data;
        } else if (error.response.data.message) {
          errorMsg = error.response.data.message;
        } else if (error.response.data.error) {
          errorMsg = error.response.data.error;
        } else if (typeof error.response.data === 'object') {
          // Se for um objeto com campos de validação
          const validationErrors = Object.entries(error.response.data)
            .map(([field, message]) => `${field}: ${message}`)
            .join(', ');
          errorMsg = `Erros de validação: ${validationErrors}`;
        }
      } else if (error?.message) {
        errorMsg = error.message;
      }
      
      setMensagem(`❌ ${errorMsg}`);
      setStatus('erro');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="meus-dados-container">
      <div className="meus-dados-header">
        <img src={Logo} alt="Logo" />
        <h2>Informações cadastrais</h2>
      </div>

      {status && <div className={`alerta ${status} fade-in`}>{mensagem}</div>}

      <form onSubmit={handleSubmit} className="meus-dados-form">
        <label>
          Nome
          <input type="text" name="fullName" value={form.fullName} onChange={handleChange} />
        </label>

        <label>
          E-mail
          <input type="email" name="email" value={form.email} onChange={handleChange} />
        </label>

        <label>
          Telefone
          <IMaskInput
            mask="(00) 00000-0000"
            name="phone"
            value={form.phone}
            onAccept={(value: any) => handleMaskedChange('phone', value)}
            placeholder="Telefone"
          />
        </label>

        <label>
          Nova senha (opcional)
          <div className="password-container">
            <input
              type={showPassword ? 'text' : 'password'}
              name="password"
              value={form.password}
              placeholder="Deixe em branco para manter a senha"
              onChange={handleChange}
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
        </label>

        <div className="botoes-container">
          <button type="button" className="botao-cancelar" onClick={() => navigate(-1)}>
            Cancelar
          </button>
          <button type="submit" className="botao-salvar" disabled={loading}>
            {loading ? 'Salvando...' : 'Salvar Alterações'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default MeusDados;