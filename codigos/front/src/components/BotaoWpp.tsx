import { useState } from 'react';
import '../styles/WhatsappWidget.css';

const WhatsappWidget = () => {
  const [open, setOpen] = useState(false);

  const handleWidgetClick = () => setOpen(!open);

  const handleWhatsappClick = () => {
  const numero = '553199217986'; 
  const mensagem = encodeURIComponent('Olá! Gostaria de falar com a equipe da Farmácia Homeopática Livremente.');
  window.open(`https://wa.me/${numero}?text=${mensagem}`, '_blank');
};

  return (
    <div>
      {/* Botão flutuante */}
      <div className={`whatsapp-fab ${open ? 'hide' : ''}`} onClick={handleWidgetClick}>
        <img src="https://upload.wikimedia.org/wikipedia/commons/6/6b/WhatsApp.svg" alt="WhatsApp" className="whatsapp-icon" />
        <span className="whatsapp-msg">Fale conosco</span>
      </div>

      {/* Widget animado */}
      <div className={`whatsapp-widget ${open ? 'open' : ''}`}>
        <div className="whatsapp-header">
          <img src="https://upload.wikimedia.org/wikipedia/commons/6/6b/WhatsApp.svg" alt="WhatsApp" className="whatsapp-header-icon" />
          <span className="whatsapp-title">Iniciar Conversa</span>
        </div>
        <div className="whatsapp-subtitle">
          A equipe normalmente responde em alguns minutos.
        </div>
        <div className="whatsapp-card" onClick={handleWhatsappClick}>
          <img src="https://upload.wikimedia.org/wikipedia/commons/6/6b/WhatsApp.svg" alt="WhatsApp" className="whatsapp-card-icon" />
          <div>
            <div className="whatsapp-card-title">Atendimento</div>
            <div className="whatsapp-card-text">CLIQUE AQUI PARA FALAR CONOSCO</div>
          </div>
          <span className="whatsapp-card-arrow">🡆</span>
        </div>
        <button className="whatsapp-close" onClick={handleWidgetClick}>×</button>
      </div>
    </div>
  );
};

export default WhatsappWidget;