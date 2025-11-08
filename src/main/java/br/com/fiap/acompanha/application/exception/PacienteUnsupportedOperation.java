package br.com.fiap.acompanha.application.exception;

public class PacienteUnsupportedOperation extends RuntimeException {
    public PacienteUnsupportedOperation(String message) {
        super(message);
    }
}
