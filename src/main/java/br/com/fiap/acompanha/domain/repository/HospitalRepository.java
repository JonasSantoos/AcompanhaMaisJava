package br.com.fiap.acompanha.domain.repository;
import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Hospital;

import java.util.List;

public interface HospitalRepository {

    Hospital salvar(Hospital hospital);
    Hospital buscarPorCnpj(String cnpj) throws EntidadeNaoLocalizada;
    List<Hospital> buscarTodos();
    Hospital excluirHospital(String cnpj, Long versao) throws EntidadeNaoLocalizada;

}
