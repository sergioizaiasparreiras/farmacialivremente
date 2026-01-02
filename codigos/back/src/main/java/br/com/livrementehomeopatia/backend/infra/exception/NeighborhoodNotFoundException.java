package br.com.livrementehomeopatia.backend.infra.exception;

public class NeighborhoodNotFoundException extends RuntimeException{
    public NeighborhoodNotFoundException(String message) {
        super(message);
    }
}
