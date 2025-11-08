package br.com.fiap.acompanha.interfaces;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.interfaces.dto.output.PacienteOutputDto;

public interface PacienteController {

    void deletar(String cpf);
    PacienteOutputDto buscarPorCpf(String cpf) throws EntidadeNaoLocalizada;

}
