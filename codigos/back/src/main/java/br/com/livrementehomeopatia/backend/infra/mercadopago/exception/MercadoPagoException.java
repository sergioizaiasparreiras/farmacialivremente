package br.com.livrementehomeopatia.backend.infra.mercadopago.exception;

/**
 * Exceção específica para erros do Checkout Pro.
 */
public class MercadoPagoException extends RuntimeException {

    public MercadoPagoException(String message) {
        super(message);
    }

    public MercadoPagoException(String message, Throwable cause) {
        super(message, cause);
    }
}