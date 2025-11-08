package br.com.fiap.acompanha.interfaces.mappers;

import br.com.fiap.acompanha.domain.model.Paciente;
import br.com.fiap.acompanha.interfaces.dto.output.PacienteOutputDto;

public class PacienteMapper {

    public static PacienteOutputDto toDto(Paciente paciente){
        if(paciente == null) return null;
        return new PacienteOutputDto(
                paciente.getIdPessoa(),
                paciente.getNome(),
                paciente.getSexo(),
                paciente.getTelefone(),
                paciente.getEndereco()
        );
    }

}
