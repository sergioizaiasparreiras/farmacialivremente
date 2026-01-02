package br.com.livrementehomeopatia.backend.infra.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message){
        super(message);
    }
}
