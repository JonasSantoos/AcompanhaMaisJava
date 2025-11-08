package br.com.fiap.acompanha.interfaces;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Cuidador;
import br.com.fiap.acompanha.interfaces.dto.output.CuidadorOutputDto;

public interface CuidadorController {

    Cuidador criar(Cuidador cuidadorInput) throws EntidadeNaoLocalizada;
    Cuidador atualizar(Cuidador cuidadorInput) throws EntidadeNaoLocalizada;
    void deletar(String cpf) throws EntidadeNaoLocalizada;
    CuidadorOutputDto buscarPorCpf(String cpf) throws EntidadeNaoLocalizada;

}
