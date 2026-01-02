package br.com.livrementehomeopatia.backend.infra.exception;

public class ExistingProductException extends RuntimeException {
    public ExistingProductException(String mensagem) {
        super(mensagem);
    }
}