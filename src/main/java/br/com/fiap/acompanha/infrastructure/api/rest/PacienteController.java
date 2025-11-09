package br.com.fiap.acompanha.infrastructure.api.rest;

import br.com.fiap.acompanha.domain.model.Paciente;
import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.service.PacienteService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/pacientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacienteController {

    @Inject
    PacienteService pacienteService;


    @GET
    public Response listarTodos() {
        try {
            List<Paciente> pacientes = pacienteService.listarTodos();
            return Response.ok(pacientes).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Erro ao listar pacientes: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/{cpf}")
    public Response buscarPorCpf(@PathParam("cpf") String cpf) throws EntidadeNaoLocalizada {
        Paciente paciente = pacienteService.buscarPorCpf(cpf);
        return Response.ok(paciente).build();
    }

    @PUT
    public Response atualizar(Paciente paciente) throws EntidadeNaoLocalizada {
        Paciente atualizado = pacienteService.atualizar(paciente);
        return Response.ok(atualizado).build();
    }

    @DELETE
    @Path("/{cpf}")
    public Response deletar(@PathParam("cpf") String cpf) throws EntidadeNaoLocalizada {
        pacienteService.excluirPaciente(cpf);
        return Response.status(Response.Status.OK).entity("{\"message\": \"Paciente deletado com sucesso\"}").build();
    }

    @GET
    @Path("/tabela-formatada")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getTabelaFormatada() {
        try {
            List<Paciente> pacientes = pacienteService.listarTodos();

            StringBuilder tabela = new StringBuilder();
            tabela.append("PACIENTE\tDATA\tHORA\tENDEREÇO\n");

            for (Paciente paciente : pacientes) {
                tabela.append(paciente.getNome()).append("\t")
                        .append("01/10").append("\t")
                        .append("15:00").append("\t")
                        .append("Endereço XYZ").append("\n");
            }

            return Response.ok(tabela.toString()).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao gerar tabela: " + e.getMessage())
                    .build();
        }
    }

}