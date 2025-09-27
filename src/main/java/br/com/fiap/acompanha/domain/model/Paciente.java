package br.com.fiap.acompanha.domain.model;

public class Paciente extends Pessoa{

    private String especialidadeAtendimento;

    public Paciente(Long idPessoa, String nome, String cpf, String dataNascimento, char sexo,
                    String telefone, String endereco, String especialidadeAtendimento, Long versao) {

        super(idPessoa, nome, cpf, dataNascimento, sexo, telefone, endereco, versao);

        this.especialidadeAtendimento = especialidadeAtendimento;
    }


    public String getEspecialidadeAtendimento() {
        return especialidadeAtendimento;
    }
}
