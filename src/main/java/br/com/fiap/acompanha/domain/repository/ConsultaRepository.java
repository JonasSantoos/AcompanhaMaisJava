package br.com.fiap.acompanha.domain.repository;

import br.com.fiap.acompanha.domain.model.Consulta;
import java.util.List;

public interface ConsultaRepository {

    List<Consulta> listarTodas();
    List<Consulta> listarPorCuidador(Long idCuidador);

}