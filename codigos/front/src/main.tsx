import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './app/App'; 
import './styles/Index.css'; 
// A importação desnecessária de 'setApiBaseUrl' é removida.
import { ThemeProvider } from './contexts/ThemeContext';

// A função 'startApp' e a chamada 'await' são removidas, pois não são mais necessárias
// com a nova configuração do arquivo api.ts. O React agora é renderizado diretamente.

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ThemeProvider>
      <App />
    </ThemeProvider>
  </React.StrictMode>
);
