package br.com.fiap.acompanha.interfaces.mappers;

import br.com.fiap.acompanha.domain.model.Cuidador;
import br.com.fiap.acompanha.interfaces.dto.output.CuidadorOutputDto;

public class CuidadorMapper {

    public static CuidadorOutputDto toDto(Cuidador cuidador){
        if(cuidador == null) return null;
        return new CuidadorOutputDto(
                cuidador.getIdPessoa(),
                cuidador.getNome(),
                cuidador.getSexo(),
                cuidador.getTelefone(),
                cuidador.getEmail(),
                cuidador.getEndereco()
        );
    }

}
