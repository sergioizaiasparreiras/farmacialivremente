import { useState, useEffect } from 'react';
import { api } from '../services/api';

interface CartItem {
  cartItemId: number;
  productId: number;
  productName: string;
  productPrice: number;
  quantity: number;
  productType: 'REVENDA' | 'HOMEOPATICO';
}

export const useCartCount = () => {
  const [cartCount, setCartCount] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);
  const [isShaking, setIsShaking] = useState<boolean>(false);

  const updateCartCount = async () => {
    try {
      setLoading(true);
      
      // Tentar carregar do backend primeiro
      try {
        const response = await api.get('/cart/items');
        const items: CartItem[] = response.data;
        const totalItems = items.reduce((total, item) => total + item.quantity, 0);
        setCartCount(totalItems);
      } catch (err) {
        // Fallback para localStorage se a API falhar
        console.warn('Erro ao carregar carrinho da API, usando localStorage:', err);
        const carrinhoSalvo = localStorage.getItem('carrinho');
        if (carrinhoSalvo) {
          const items: CartItem[] = JSON.parse(carrinhoSalvo);
          const totalItems = items.reduce((total, item) => total + item.quantity, 0);
          setCartCount(totalItems);
        } else {
          setCartCount(0);
        }
      }
    } catch (err) {
      console.error('Erro ao atualizar contagem do carrinho:', err);
      setCartCount(0);
    } finally {
      setLoading(false);
    }
  };

  // Função para disparar animação de balanço
  const triggerShake = () => {
    setIsShaking(true);
    setTimeout(() => setIsShaking(false), 600); // Duração da animação
  };

  // Função para ser chamada quando produtos são adicionados/removidos
  const refreshCartCount = () => {
    triggerShake();
    updateCartCount();
  };

  useEffect(() => {
    updateCartCount();

    // Escutar mudanças no localStorage
    const handleStorageChange = (e: StorageEvent) => {
      if (e.key === 'carrinho') {
        updateCartCount();
      }
    };

    window.addEventListener('storage', handleStorageChange);

    // Listener customizado para mudanças no carrinho
    const handleCartUpdate = () => {
      triggerShake();
      updateCartCount();
    };

    window.addEventListener('cartUpdated', handleCartUpdate);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
      window.removeEventListener('cartUpdated', handleCartUpdate);
    };
  }, []);

  return {
    cartCount,
    loading,
    refreshCartCount,
    isShaking
  };
}; 