import { api } from './api';

/**
 * Obtém todos os produtos do sistema
 */
export const getAllProdutos = () => {
  return api.get('/product');
};

/**
 * Busca um produto pelo ID
 * @param id ID do produto
 */
export const getProdutoById = (id: number) => {
  return api.get(`/product/${id}`);
};

/**
 * Cria um novo produto
 * @param produto Dados do produto como FormData
 */
export const createProduto = (produto: FormData) => {
  return api.post('/product', produto); // rota correta
};

/**
 * Atualiza um produto existente
 * @param id ID do produto
 * @param produto Dados atualizados do produto como FormData
 */
export const updateProduto = (id: number, produto: FormData) => {
  return api.put(`/product/${id}`, produto);
};

/**
 * Exclui um produto pelo ID
 * @param id ID do produto
 */
export const deleteProduto = (id: number) => {
  return api.delete(`/product/${id}`);
};
