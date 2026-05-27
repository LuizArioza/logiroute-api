package com.logiroute.api.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String recurso, Long id) {
        super("%s não encontrado com id: %d".formatted(recurso, id));
    }
}