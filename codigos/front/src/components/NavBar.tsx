import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FaShoppingCart, FaSun, FaMoon, FaUserMd, FaUser } from 'react-icons/fa';
import { WiDaySunny, WiDaySunnyOvercast, WiNightClear } from 'react-icons/wi';
import { jwtDecode } from 'jwt-decode';
import { useTheme } from '../contexts/ThemeContext';
import { useCartCount } from '../hooks/useCartCount';
import '../styles/NavBar.css';

interface NavBarProps {
  nomeLogo: string;
  imgLogo: string;
  barrapesquisa: string;
}

interface DecodedToken {
  role?: string;
  exp?: number;
}

const NavBar: React.FC<NavBarProps> = ({ nomeLogo, imgLogo }) => {
  // Inicializar estados com valores padrão
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRole, setUserRole] = useState<string | null>(null);
  const [userName, setUserName] = useState<string>('');
  const [showMenu, setShowMenu] = useState(false);
  const navigate = useNavigate();
  const { isDarkMode, toggleTheme } = useTheme();
  const { cartCount, isShaking } = useCartCount();

  // Função para verificar o token e atualizar os estados
  const checkToken = () => {
    const token = localStorage.getItem('token');
    
    if (!token) {
      setIsAuthenticated(false);
      setUserRole(null);
      setUserName('');
      return;
    }
    
    try {
      const decoded = jwtDecode<DecodedToken>(token);
      const currentTime = Date.now() / 1000;
      
      if (decoded.exp && decoded.exp < currentTime) {
        // Token expirado
        localStorage.removeItem('token');
        localStorage.removeItem('userName');
        localStorage.removeItem('userEmail');
        localStorage.removeItem('userRole');
        setIsAuthenticated(false);
        setUserRole(null);
        setUserName('');
      } else {
        // Token válido
        setUserRole(decoded.role ?? null);
        setUserName(localStorage.getItem('userName') || '');
        setIsAuthenticated(true);
      }
    } catch (error) {
      // Token inválido
      localStorage.removeItem('token');
      localStorage.removeItem('userName');
      localStorage.removeItem('userEmail');
      localStorage.removeItem('userRole');
      setIsAuthenticated(false);
      setUserRole(null);
      setUserName('');
    }
  };

  // Verificar token ao montar o componente
  useEffect(() => {
    checkToken();
    
    // Verificar o token a cada 30 segundos (reduzido de 5 para evitar verificações excessivas)
    const interval = setInterval(checkToken, 30000);
    
    // Listener para atualização de dados do usuário
    const handleUserDataUpdate = (event: CustomEvent) => {
      console.log('🔄 Evento userDataUpdated recebido na navbar:', event.detail);
      if (event.detail && event.detail.fullName) {
        console.log('🏷️ Atualizando nome na navbar de:', userName, 'para:', event.detail.fullName);
        setUserName(event.detail.fullName);
      }
    };
    
    // Listener para mudanças no localStorage
    const handleStorageChange = (e: StorageEvent) => {
      console.log('💾 Storage change detectado:', e.key, e.newValue);
      if (e.key === 'userName' && e.newValue) {
        console.log('👤 Atualizando nome via storage de:', userName, 'para:', e.newValue);
        setUserName(e.newValue);
      }
    };
    
    // Função para verificar manualmente o localStorage
    const checkUserNameUpdate = () => {
      const currentName = localStorage.getItem('userName');
      if (currentName && currentName !== userName) {
        console.log('🔍 Verificação manual: nome atualizado para:', currentName);
        setUserName(currentName);
      }
    };

    window.addEventListener('userDataUpdated', handleUserDataUpdate as EventListener);
    window.addEventListener('storage', handleStorageChange);
    
    // Verificar mudanças no localStorage a cada 2 segundos como fallback
    const nameCheckInterval = setInterval(checkUserNameUpdate, 2000);
    
    return () => {
      clearInterval(interval);
      clearInterval(nameCheckInterval);
      window.removeEventListener('userDataUpdated', handleUserDataUpdate as EventListener);
      window.removeEventListener('storage', handleStorageChange);
    };
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userRole');
    setIsAuthenticated(false);
    setUserRole(null);
    setUserName('');
    navigate('/login');
  };

  const handleGoToMeusDados = () => {
    setShowMenu(false);
    navigate('/meus-dados');
  };

  const handleGoToMeusPedidos = () => {
    setShowMenu(false);
    navigate('/meus-pedidos');
  };

  // Função para obter as iniciais do nome do usuário
  const getUserInitials = () => {
    if (!userName) return 'U';
    
    const names = userName.trim().split(' ');
    if (names.length === 1) {
      return names[0].charAt(0).toUpperCase();
    }
    
    // Primeira letra do primeiro nome + primeira letra do último nome
    return (names[0].charAt(0) + names[names.length - 1].charAt(0)).toUpperCase();
  };

  // Função para obter o tipo de usuário em português
  const getUserTypeLabel = () => {
    if (userRole === 'ROLE_MEDICO') return 'Médico(a)';
    if (userRole === 'ROLE_CLIENTE') return 'Cliente';
    return 'Usuário';
  };

  // Classe CSS única para todos os usuários
  const getUserIconClass = () => {
    return 'usuario-icone';
  };

  // Função para renderizar o ícone/conteúdo do usuário
  const renderUserIcon = () => {
    if (userRole === 'ROLE_MEDICO') {
      return <FaUserMd size={18} />;
    }
    if (userRole === 'ROLE_CLIENTE') {
      return <FaUser size={17} />;
    }
    return <span style={{ fontSize: '14px', fontWeight: '600' }}>{getUserInitials()}</span>;
  };

  // Função para obter a saudação e ícone baseado no horário
  const getSaudacaoData = () => {
    const agora = new Date();
    const hora = agora.getHours();
    const minutos = agora.getMinutes();
    
    if (hora >= 5 && hora < 12) {
      return {
        texto: 'Bom dia',
        icone: <WiDaySunny size={24} />,
        classe: 'manha'
      };
    } else if (hora === 12 && minutos === 0) {
      return {
        texto: 'Bom dia',
        icone: <WiDaySunny size={24} />,
        classe: 'manha'
      };
    } else if ((hora === 12 && minutos > 0) || (hora > 12 && hora < 18)) {
      return {
        texto: 'Boa tarde',
        icone: <WiDaySunnyOvercast size={24} />,
        classe: 'tarde'
      };
    } else {
      return {
        texto: 'Boa noite',
        icone: <WiNightClear size={24} />,
        classe: 'noite'
      };
    }
  };

  return (
    <div className="nav">
      <Link to="/" className="logo" style={{ textDecoration: 'none' }}>
        <div className="img_Logo">
          <img id="logoIMG" src={imgLogo} alt="Logo" />
        </div>
        <div className="Nome_Logo">
          <h1 id="titulo">{nomeLogo}</h1>
        </div>
      </Link>

      <div className="saudacao-container">
        {isAuthenticated && userName && (() => {
          const saudacaoData = getSaudacaoData();
          return (
            <div className={`saudacao-completa ${saudacaoData.classe}`}>
              <span className="saudacao-icone">{saudacaoData.icone}</span>
              <span className="saudacao-texto">
                {saudacaoData.texto}, {userName.split(' ')[0]}!
              </span>
            </div>
          );
        })()}
      </div>

      <div className="acoes-direita">
        {/* Botão de alternância de tema */}
        <button 
          className="theme-toggle" 
          onClick={toggleTheme}
          title={isDarkMode ? 'Modo Claro' : 'Modo Escuro'}
        >
          {isDarkMode ? <FaSun /> : <FaMoon />}
        </button>

        {!isAuthenticated ? (
          // Usuário NÃO autenticado - mostrar links de login/registro
          <div className="nav-links">
            <Link to="/login" className="nav-link">ENTRE</Link>
            <Link to="/registro" className="nav-link">CADASTRE-SE</Link>
          </div>
        ) : (
          // Usuário autenticado - mostrar ícone e carrinho
          <div className="usuario-wrapper">
            <button 
              className={`carrinho-icon ${isShaking ? 'shake' : ''}`}
              title="Carrinho" 
              onClick={() => navigate('/carrinho-de-compras')}
            >
              <FaShoppingCart size={18} />
              {cartCount > 0 && (
                <span className="carrinho-badge">{cartCount > 99 ? '99+' : cartCount}</span>
              )}
            </button>

            <div className="usuario-container">
              <div 
                className={getUserIconClass()} 
                onClick={() => setShowMenu((prev) => !prev)}
                title={`${userName} - ${getUserTypeLabel()}`}
              >
                {/* Mostra apenas o ícone diferenciado */}
                {renderUserIcon()}
              </div>

              {showMenu && (
                <div className="usuario-menu">
                  <div className="usuario-info">
                    <strong>{userName}</strong>
                    <span className="usuario-tipo">{getUserTypeLabel()}</span>
                  </div>
                  <div className="menu-divider"></div>
                  <button onClick={handleGoToMeusDados}>Meus Dados</button>
                  <button onClick={handleGoToMeusPedidos}>Meus Pedidos</button>
                  <button onClick={handleLogout}>Sair</button>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default NavBar;