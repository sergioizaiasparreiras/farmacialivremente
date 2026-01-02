package br.com.livrementehomeopatia.backend.infra.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String mensagem) {
        super(mensagem);
    }
}