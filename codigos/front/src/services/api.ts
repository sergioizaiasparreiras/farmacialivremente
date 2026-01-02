import axios from 'axios';

// 1. Cria a instância do axios com a baseURL correta para produção e desenvolvimento.
// O caminho relativo '/api' será tratado pelo Nginx na Azure ou pelo proxy do Vite localmente.
export const api = axios.create({
  baseURL: '/api'
});

// 2. Mantém o interceptor, que é essencial para adicionar o token de autenticação.
api.interceptors.request.use(
  (config) => {
    // Pega o token do localStorage (ajuste se você armazena em outro lugar)
    const token = localStorage.getItem('token');
    
    if (token) {
      config.headers = config.headers || {};
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// A função complexa 'setApiBaseUrl' foi removida, pois não é necessária.

// Não se esqueça de exportar a instância 'api' se outros ficheiros a usarem diretamente.
// Se a sua exportação era 'export default api', pode manter assim.
