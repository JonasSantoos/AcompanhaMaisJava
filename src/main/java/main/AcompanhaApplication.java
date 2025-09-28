package main;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Cuidador;
import br.com.fiap.acompanha.domain.model.Hospital;
import br.com.fiap.acompanha.domain.model.Paciente;
import br.com.fiap.acompanha.domain.repository.CuidadorRepository;
import br.com.fiap.acompanha.domain.repository.HospitalRepository;
import br.com.fiap.acompanha.domain.repository.PacienteRepository;

import io.quarkus.runtime.QuarkusApplication;
import jakarta.inject.Inject;

import java.util.List;

public class AcompanhaApplication implements QuarkusApplication {

    @Inject
    CuidadorRepository cuidadorRepository;

    @Inject
    HospitalRepository hospitalRepository;

    @Inject
    PacienteRepository pacienteRepository;

    @Override
    public int run(String... args) throws Exception {

        //cuidador
        Cuidador cuidador1 = new Cuidador(1L, "Jonas Santos", "11111111111", "31/07/2005", 'M', "11911111111", "R. Estrela Dalva, 08 - SBC", "jonas@gmail.com", "123456", 1L);

        adicionarCuidador(cuidador1);
        buscarCuidadorPorCpf("11111111111");
        editarCuidador(new Cuidador(1L, "Jonas Editado", "11111111111", "31/07/2005", 'M', "11922222222", "R. Nova Rua, 123", "jonas_editado@gmail.com", "654321", 1L));
        listarCuidadores();
        excluirCuidador("11111111111", 2L);

        //paciente
        buscarPacientePorCpf("22222222222");
        listarPacientes();
        excluirPaciente("22222222222", 1L);

        //hospital
        Hospital hospital1 = new Hospital(1L, "Hospital Central", "12345678000100", "Av. Brasil, 1000", "hcentral@hotmail.com", 1L);

        salvarHospital(hospital1);
        buscarHospitalPorCnpj("12345678000100");
        listarHospitais();
        excluirHospital("12345678000100", 1L);

        return 0;
    }

    //Métodos
    private void adicionarCuidador(Cuidador cuidador) {
        cuidadorRepository.adicionar(cuidador);
        System.out.println("Cuidador adicionado");
    }

    private void buscarCuidadorPorCpf(String cpf) {
        try {
            Cuidador c = cuidadorRepository.buscarPorCpf(cpf);
            System.out.println("Cuidador encontrado: ");
            System.out.println(c);
        } catch (EntidadeNaoLocalizada e) {
            System.out.println("Cuidador não encontado");
        }
    }

    private void editarCuidador(Cuidador cuidador) {
        try {
            Cuidador c = cuidadorRepository.editar(cuidador);
            System.out.println("Cuidador editado");
            System.out.println(c);
        } catch (RuntimeException e) {
            System.out.println("Erro ao editar cuidador");
        }
    }

    private void listarCuidadores() {
        List<Cuidador> lista = cuidadorRepository.buscarTodos();
        System.out.println("Lista de cuidadores: ");
        lista.forEach(System.out::println);
    }

    private void excluirCuidador(String cpf, Long versao) {
        try {
            Cuidador c = cuidadorRepository.excluirCuidador(cpf, versao);
            System.out.println("Cuidador excluído: ");
            System.out.println(c);
        } catch (RuntimeException e) {
            System.out.println("Cuidador não foi excluído.");
        }
    }

    private void buscarPacientePorCpf(String cpf) {
        try {
            Paciente p = pacienteRepository.buscarPorCpf(cpf);
            System.out.println("Paciente encontrado: ");
            System.out.println(p);
        } catch (EntidadeNaoLocalizada e) {
            System.out.println("Paciente não encontrado.");
        }
    }

    private void listarPacientes() {
        List<Paciente> lista = pacienteRepository.buscarTodos();
        System.out.println("Lista de pacientes:");
        lista.forEach(System.out::println);
    }

    private void excluirPaciente(String cpf, Long versao) {
        try {
            Paciente p = pacienteRepository.excluirPaciente(cpf, versao);
            System.out.println("Paciente removido: ");
            System.out.println(p);
        } catch (EntidadeNaoLocalizada e) {
            System.out.println("Paciente não foi excluído.");
        }
    }

    private void salvarHospital(Hospital hospital) {
        hospitalRepository.salvar(hospital);
        System.out.println("Hospital salvo: ");
        System.out.println(hospital);
    }

    private void buscarHospitalPorCnpj(String cnpj) {
        try {
            Hospital h = hospitalRepository.buscarPorCnpj(cnpj);
            System.out.println("Hospital encontrado: ");
            System.out.println(h);
        } catch (EntidadeNaoLocalizada e) {
            System.out.println("Hospital não encontrado.");
        }
    }

    private void listarHospitais() {
        List<Hospital> lista = hospitalRepository.buscarTodos();
        System.out.println("Lista de hospitais: ");
        lista.forEach(System.out::println);
    }

    private void excluirHospital(String cnpj, Long versao) {
        try {
            Hospital h = hospitalRepository.excluirHospital(cnpj, versao);
            System.out.println("Hospital excluído: ");
            System.out.println(h);
        } catch (EntidadeNaoLocalizada e) {
            System.out.println("Hospital não foi excluído.");
        }
    }
}