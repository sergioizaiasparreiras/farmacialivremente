package br.com.livrementehomeopatia.backend.validation;

/**
 * Interface de marcação utilizada para agrupar validações condicionais.
 * Pode ser usada em anotações de validação para indicar que a regra só deve ser aplicada
 * quando determinado campo estiver presente (não nulo).
 *
 * Exemplo típico de uso: validações parciais em atualizações de dados (PATCH).
 */
public interface OnFieldPresent {}
