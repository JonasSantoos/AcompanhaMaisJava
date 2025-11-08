package br.com.fiap.acompanha.interfaces;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Paciente;
import br.com.fiap.acompanha.domain.service.PacienteService;
import br.com.fiap.acompanha.interfaces.dto.output.PacienteOutputDto;
import br.com.fiap.acompanha.interfaces.mappers.PacienteMapper;

public class PacienteControllerImpl implements PacienteController{

    private final PacienteService pacienteService;

    public PacienteControllerImpl(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }


    @Override
    public void deletar(String cpf) {
        this.pacienteService.excluirPaciente(cpf);
    }

    @Override
    public PacienteOutputDto buscarPorCpf(String cpf) throws EntidadeNaoLocalizada {
        Paciente paciente = this.pacienteService.buscarPorCpf(cpf);
        return PacienteMapper.toDto(paciente);
    }
}
