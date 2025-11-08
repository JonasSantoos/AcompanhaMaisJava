package br.com.fiap.acompanha.infrastructure.api.rest;

import br.com.fiap.acompanha.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.acompanha.domain.model.Cuidador;
import br.com.fiap.acompanha.domain.service.CuidadorService;
import br.com.fiap.acompanha.interfaces.dto.output.CuidadorListagemDto;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/cuidadores")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CuidadorController {

    @Inject
    CuidadorService cuidadorService;

    @POST
    public Response criar(Cuidador cuidador) throws EntidadeNaoLocalizada {
        Cuidador novo = cuidadorService.criar(cuidador);
        return Response.status(Response.Status.CREATED).entity(novo).build();
    }

    @POST
    @Path("/login")
    public Response login(@QueryParam("email") String email,
                          @QueryParam("senha") String senha) {
        try {
            Cuidador cuidador = cuidadorService.buscarPorEmail(email);

            if (cuidador != null && cuidador.getSenha().equals(senha)) {
                Cuidador cuidadorCompleto = cuidadorService.buscarPorCpf(cuidador.getCpf());
                return Response.ok(cuidadorCompleto).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Email ou senha inválidos\"}")
                        .build();
            }
        } catch (EntidadeNaoLocalizada e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Email ou senha inválidos\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Erro ao fazer login\"}")
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarTodos() {
        try {
            List<Cuidador> cuidadores = cuidadorService.listarTodos();

            if (cuidadores == null || cuidadores.isEmpty()) {
                return Response.ok().entity("[]").build();
            }

            List<CuidadorListagemDto> dtos = new ArrayList<>();
            for (Cuidador cuidador : cuidadores) {
                CuidadorListagemDto dto = new CuidadorListagemDto(
                        cuidador.getIdPessoa(),
                        cuidador.getNome(),
                        cuidador.getCpf(),
                        cuidador.getEmail(),
                        cuidador.getTelefone()
                );
                dtos.add(dto);
            }

            return Response.ok(dtos).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Erro ao listar cuidadores\"}")
                    .build();
        }
    }

    @GET
    @Path("/{cpf}")
    public Response buscarPorCpf(@PathParam("cpf") String cpf) throws EntidadeNaoLocalizada {
        Cuidador cuidador = cuidadorService.buscarPorCpf(cpf);
        return Response.ok(cuidador).build();
    }

    @PUT
    public Response atualizar(Cuidador cuidador) throws EntidadeNaoLocalizada {
        Cuidador atualizado = cuidadorService.atualizar(cuidador);
        return Response.ok(atualizado).build();
    }

    @DELETE
    @Path("/{cpf}")
    public Response deletar(@PathParam("cpf") String cpf) throws EntidadeNaoLocalizada {
        cuidadorService.deletarCuidador(cpf);
        return Response.noContent().build();
    }

    @POST
    @Path("/{cpfCuidador}/vincular/{cpfPaciente}")
    public Response vincularPaciente(@PathParam("cpfCuidador") String cpfCuidador,
                                     @PathParam("cpfPaciente") String cpfPaciente) throws EntidadeNaoLocalizada {
        Cuidador atualizado = cuidadorService.vincularPaciente(cpfCuidador, cpfPaciente);
        return Response.ok(atualizado).build();
    }

    @PUT
    @Path("/{cpf}/senha")
    public Response atualizarSenha(@PathParam("cpf") String cpf,
                                   @QueryParam("novaSenha") String novaSenha) throws EntidadeNaoLocalizada {
        try {
            Cuidador cuidador = cuidadorService.buscarPorCpf(cpf);
            cuidador.setSenha(novaSenha);
            Cuidador atualizado = cuidadorService.atualizar(cuidador);
            Cuidador confirmacao = cuidadorService.buscarPorCpf(cpf);
            return Response.ok(confirmacao).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Erro ao atualizar senha\"}")
                    .build();
        }
    }
}