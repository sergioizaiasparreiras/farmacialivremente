import React from 'react';
import '../styles/Footer.css';
import { FaEnvelope, FaFacebookF, FaInstagram, FaLinkedinIn, FaMapMarkerAlt, FaPhone, FaTwitter, FaWhatsapp } from 'react-icons/fa';

const Footer: React.FC = () => {
  return (
    <footer className="footer">
      <div className="footer-content">
        <div className="footer-section">
          <h3>Institucional</h3>
          <ul>
            <li>
              <a href="#">Sobre nós</a>
            </li>
            <li>
              <a href="#">Nossa História</a>
            </li>
            <li>
              <a href="#">Trabalhe Conosco</a>
            </li>
            <li>
              <a href="#">Política de Privacidade</a>
            </li>
          </ul>
        </div>

        <div className="footer-section">
          <h3>Produtos</h3>
          <ul>
            <li>
              <a href="#">Medicamentos</a>
            </li>
            <li>
              <a href="#">Dermocosméticos</a>
            </li>
            <li>
              <a href="#">Higiene</a>
            </li>
            <li>
              <a href="#">Infantil</a>
            </li>
            <li>
              <a href="#">Suplementos</a>
            </li>
          </ul>
        </div>

        <div className="footer-section">
          <h3>Atendimento</h3>
          <ul>
            <li>
              <a href="#">Central de Ajuda</a>
            </li>
            <li>
              <a href="#">Fale Conosco</a>
            </li>
            <li>
              <a href="#">Ouvidoria</a>
            </li>
            <li>
              <a href="#">SAC</a>
            </li>
          </ul>
        </div>

        <div className="footer-section">
          <h3 className="text-xl font-bold mb-4">Contato</h3>

          <p className="flex items-center gap-2">
            <FaPhone /> (31) 3221-9641 / (31) 3227-7597
          </p>

          <p className="flex items-center gap-2">
            <FaWhatsapp />
            <a
              href="https://wa.me/5531999217986"
              target="_blank"
              rel="noopener noreferrer"
              className="hover:underline"
            >
              (31) 99921-7986
            </a>
          </p>

          <p className="flex items-center gap-2">
            <FaEnvelope /> contato@farmacialivremente.com.br
          </p>

          <p className="flex items-center gap-2">
            <FaMapMarkerAlt />
            R. Desembargador Drumond, 112 – Serra, Belo Horizonte / MG
          </p>

          <div className="social-links flex gap-4 mt-4 text-white">
            <a href="#" title="Facebook">
              <FaFacebookF />
            </a>
            <a href="#" title="Instagram">
              <FaInstagram />
            </a>
            <a href="#" title="Twitter">
              <FaTwitter />
            </a>
            <a href="#" title="LinkedIn">
              <FaLinkedinIn />
            </a>
          </div>
        </div>
      </div>

      <div className="footer-bottom mt-6 text-sm text-center">
        <p>© 2024 Farmácia Livremente. Todos os direitos reservados.</p>
      </div>
    </footer>
  );
};

export default Footer;