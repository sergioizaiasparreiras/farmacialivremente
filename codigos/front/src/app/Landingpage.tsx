import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import '../styles/LandingPage.css';
import carrossel1 from '../assets/Carrossel01.png';
import carrossel2 from '../assets/Carrossel02.png';
import carrossel3 from '../assets/Carrossel03.png';
import GoogleLogo from '../assets/google-image.png';
import SimboloGoogle from '../assets/simbolo-google.png';

const imagens = [carrossel1, carrossel2, carrossel3];

interface DecodedToken {
  role?: string;
}

const LandingPage: React.FC = () => {
  const [index, setIndex] = useState(0);
  const [userRole, setUserRole] = useState<string | null>(null);
  const [parallaxStyle, setParallaxStyle] = useState<React.CSSProperties>({});
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decoded = jwtDecode<DecodedToken>(token);
        setUserRole(decoded.role ?? null);
      } catch {
        setUserRole(null);
      }
    } else {
      setUserRole(null); // Alterado para null quando não há token
    }
  }, []);

  useEffect(() => {
    const intervalo = setInterval(() => {
      setIndex((prevIndex) => (prevIndex + 1) % imagens.length);
    }, 15000);
    return () => clearInterval(intervalo);
  }, []);

  const irParaAnterior = () => {
    setIndex((prev) => (prev - 1 + imagens.length) % imagens.length);
  };

  const irParaProximo = () => {
    setIndex((prev) => (prev + 1) % imagens.length);
  };

  const handleInsumoClick = (e: React.MouseEvent) => {
    e.preventDefault();
    if (userRole !== 'MEDICO' && userRole !== 'ROLE_MEDICO') {
  navigate('/acesso-negado');
} else {
  navigate('/insumos/consultar');
}

  };

  const handleSolicitarOrcamentoClick = () => {
  const numero = '553199217986'; 
  const mensagem = encodeURIComponent('Olá! Gostaria de solicitar um orçamento homeopático.');
  window.open(`https://wa.me/${numero}?text=${mensagem}`, '_blank');
};

  // Parallax effect for carousel
  const handleParallax = (e: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    const { left, width, top, height } = e.currentTarget.getBoundingClientRect();
    const x = e.clientX - left;
    const y = e.clientY - top;
    // Calculate offset from center
    const offsetX = (x - width / 2) / (width / 2);
    const offsetY = (y - height / 2) / (height / 2);
    setParallaxStyle({
      transform: `scale(1.05) translate(${offsetX * 20}px, ${offsetY * 10}px)`,
      transition: 'transform 0.2s cubic-bezier(.25,.46,.45,.94)',
      willChange: 'transform',
    });
  };

  // Reset parallax when mouse leaves
  const handleParallaxLeave = () => {
    setParallaxStyle({
      transform: 'scale(1)',
      transition: 'transform 0.5s cubic-bezier(.25,.46,.45,.94)',
    });
  };

  // --- Carrossel de avaliações ---
  const avaliacoes = [
    {
      nome: 'Marcela Magalhães',
      data: '2023-05-18',
      avatar: '',
      texto: 'Atendimento excelente; O melhor preço do mercado; Entrega rápida e com o frete que cabe no bolso!',
    },
    {
      nome: 'Karla Magalhaes Silva',
      data: '2023-05-17',
      avatar: '',
      texto: 'Excelente preço, atendimento rápido e gentil. Melhor preço encontrado no produto que precisei.',
    },
    {
      nome: 'Eliane Guglielmelli',
      data: '2023-05-17',
      avatar: '',
      texto: 'Atendimento e produtos de qualidade',
    },
    {
      nome: 'Kátia Kellen',
      data: '2023',
      avatar: '',
      texto: 'Confio na procedência de olhos fechados!!!',
    },
    {
      nome: 'Iaci Carneiro',
      data: '2023',
      avatar: '',
      texto: '',
    },
    {
      nome: 'Rogério Andrade',
      data: '2019',
      avatar: '',
      texto: '',
    },
  ];
  const [avaliacaoIndex, setAvaliacaoIndex] = useState(0);
  const maxIndex = avaliacoes.length - 3;
  const handlePrevAvaliacao = () => setAvaliacaoIndex((prev) => Math.max(prev - 1, 0));
  const handleNextAvaliacao = () => setAvaliacaoIndex((prev) => Math.min(prev + 1, maxIndex));

  return (
    <div className="landing-page">
      {/* HERO COM CARROSSEL */}
      <section className="hero-section">
        
        <div className="carousel-container parallax-carousel" onMouseMove={handleParallax} onMouseLeave={handleParallaxLeave}>
          {imagens.map((img, i) => (
            <img
              key={i}
              src={img}
              alt={`Slide ${i + 1}`}
              className={`carousel-image ${i === index ? 'active' : ''}`}
              style={i === index ? parallaxStyle : {}}
            />
          ))}
          <button className="arrow left" onClick={irParaAnterior}>
            ❮
          </button>
          <button className="arrow right" onClick={irParaProximo}>
            ❯
          </button>
          <div className="indicators">
            {imagens.map((_, i) => (
              <span key={i} className={`dot ${i === index ? 'active' : ''}`}></span>
            ))}
          </div>
        </div>

        <div className="card">
          <div className="header">
            <div className="content">
              <span className="title">Quem somos?</span>
              <p className="message">
                Há mais de 26 anos, somos uma farmácia homeopática dedicada a oferecer tratamentos
                seguros e personalizados, baseados nos princípios da homeopatia. Nossa missão é
                proporcionar uma abordagem holística à saúde, respeitando a individualidade de cada
                paciente. Com uma equipe qualificada e compromisso com a qualidade, buscamos
                equilíbrio e bem-estar para nossos clientes, sempre atuando com ética e excelência.
              </p>
            </div>
            <div className="actions">
              <button 
                className="solicitarOrcamento" 
                type="button"
                onClick={handleSolicitarOrcamentoClick}
              >
                Solicitar Orçamento
              </button>
              <button className="saibaMais" type="button">
                Saiba mais
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* MENU DE CATEGORIAS - AGORA COM LINKS */}
      <section className="categorias">
        <ul className="categorias-lista">
          <li className="categoria-item">
            <Link to="/pagina-de-produtos">PRODUTOS</Link>
          </li>
          <li className="categoria-item">
            <Link to="">INFORMATIVOS</Link>
          </li>
          <li className="categoria-item">
            <Link to="">PEDIDOS</Link>
          </li>
          <li className="categoria-item">
            <Link to="">QUEM SOMOS</Link>
          </li>
          <li className="categoria-item">
            <Link to="">ENTRE EM CONTATO</Link>
          </li>
          <li className="categoria-item">
            <a href="#" onClick={handleInsumoClick}>INSUMOS</a>
          </li>
        </ul>
      </section>

      {/* INFO CARDS */}
      <section className="info-cards-container">
        <div>
          <div className="info-card">
            <h3>Entrega Rápida</h3>
            <p>
              Receba seus medicamentos no conforto da sua casa com nossa entrega expressa e segura.
            </p>
          </div>
          <div className="info-card">
            <h3>Atendimento de qualidade</h3>
            <p>Conte com nossa equipe especializada para um atendimento eficaz.</p>
          </div>
          <div className="info-card">
            <h3>Melhor Preço</h3>
            <p>Garantimos os melhores preços do mercado com descontos especiais em medicamentos.</p>
          </div>
        </div>
      </section>

    {/* LOCALIZAÇÃO */}
<section className="secaoLocalizacao">
  <div className="containerLocalizacao">
    <div className="mapaContainer">
      <iframe
        className="mapaContainer"
        loading="lazy"
        referrerPolicy="no-referrer-when-downgrade"
        allowFullScreen
        title="Localização da Farmácia Livremente"
        src="https://www.google.com/maps/embed/v1/place?key=AIzaSyAC_qvMMwk11KglutDnIJF9tzg23hYaSuY&q=Rua+Desembargador+Drumond,112,Serra,Belo+Horizonte,MG"
      ></iframe>
    </div>

    <div className="infoLocalizacao">
  <h2>Onde estamos?</h2>
  <p>
    A <strong>Farmácia Homeopática Livremente</strong> foi fundada em 1994 por
    Ione Lima Magalhães, farmacêutica formada pela UFOP e especialista em Homeopatia.
    Há mais de 30 anos, oferecemos manipulação de medicamentos homeopáticos com um
    atendimento próximo e personalizado, tanto para nossos clientes quanto para médicos parceiros.
  </p>


      <div className="endereco">
        <h3>Nosso Endereço</h3>
        <p>R. Desembargador Drumond, 112</p>
        <p>Bairro Serra</p>
        <p>Belo Horizonte - MG</p>
        <p>CEP: 30210-040</p>
        <p>
          WhatsApp:{" "}
          <a
            href="https://wa.me/5531999217986"
            target="_blank"
            rel="noopener noreferrer"
            style={{ color: "#25D366", textDecoration: "underline" }}
          >
            (31) 99921-7986
          </a>
        </p>
      </div>
    </div>
  </div>
</section>


      {/* AVALIAÇÕES */}
      <section className="secaoAvaliacoes">
        <div className="containerAvaliacoes novo-layout-google">
          <div className="tituloAvaliacoes">
            <h2>O QUE DIZEM NOSSOS CLIENTES</h2>
          </div>
          <div className="google-rating-header">
            <img src={GoogleLogo} alt="Google" className="google-logo" style={{ display: 'block', margin: '0 auto', width: 120, height: 'auto', maxWidth: '100%' }} />
            <div className="google-stars">
              {Array(5).fill(0).map((_, i) => (
                <span key={i} style={{ color: '#FFD600', fontSize: 32, marginRight: 2 }}>★</span>
              ))}
            </div>
            <div className="google-rating-info">
              <span>Com base em 544 avaliações</span>
            </div>
          </div>
          <div className="avaliacoes-carousel-wrapper">
            <button className="carousel-arrow left" onClick={handlePrevAvaliacao} disabled={avaliacaoIndex === 0}>&#10094;</button>
            <div className="avaliacoes-cards-wrapper" style={{overflow: 'hidden'}}>
              {avaliacoes.slice(avaliacaoIndex, avaliacaoIndex + 3).map((avaliacao, i) => (
                <div key={i} className="cardAvaliacao novo-google-card">
                  <div className="headerAvaliacao">
                    <div className="avatarAvaliacao">
                      {avaliacao.avatar ? (
                        <img src={avaliacao.avatar} alt={avaliacao.nome} />
                      ) : (
                        <div className="avatar-placeholder">{avaliacao.nome[0]}</div>
                      )}
                    </div>
                    <div className="infoAvaliador">
                      <span className="nomeAvaliador">{avaliacao.nome}</span>
                      <span className="dataAvaliacao">{avaliacao.data ? new Date(avaliacao.data).toLocaleDateString('pt-BR') : ''}</span>
                    </div>
                    <img src={SimboloGoogle} alt="Google" className="mini-google-logo" style={{ width: 24, height: 24, marginLeft: 8 }} />
                  </div>
                  <div className="estrelas">{Array(5).fill(0).map((_, i) => <span key={i} style={{ color: '#FFD600', fontSize: 20 }}>★</span>)}</div>
                  {avaliacao.texto && <p className="textoAvaliacao">{avaliacao.texto}</p>}
                </div>
              ))}
            </div>
            <button className="carousel-arrow right" onClick={handleNextAvaliacao} disabled={avaliacaoIndex === maxIndex}>&#10095;</button>
          </div>
        </div>
      </section>
    </div>
  );
};

export default LandingPage;