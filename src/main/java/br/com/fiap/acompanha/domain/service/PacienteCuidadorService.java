package br.com.fiap.acompanha.domain.service;

public interface PacienteCuidadorService {

    void vincular(String cpfCuidador, String cpfPaciente);
    void desvincular(String cpfCuidador, String cpfPaciente);
    boolean isPacienteDoCuidador(String cpfCuidador, String cpfPaciente);

}
