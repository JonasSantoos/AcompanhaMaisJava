package br.com.fiap.acompanha.infrastructure.api.rest;

import br.com.fiap.acompanha.domain.model.Consulta;
import br.com.fiap.acompanha.domain.repository.ConsultaRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.text.SimpleDateFormat;
import java.util.List;

@Path("/consultas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConsultaController {

    @Inject
    ConsultaRepository consultaRepository;

    @GET
    @Path("/tabela-formatada")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getTabelaFormatada() {
        try {
            List<Consulta> consultas = consultaRepository.listarTodas();

            StringBuilder tabela = new StringBuilder();
            tabela.append("PACIENTE\tDATA\tHORA\tENDEREÇO\n");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");

            for (Consulta consulta : consultas) {
                String data = consulta.getDataConsulta() != null ?
                        dateFormat.format(consulta.getDataConsulta()) : "N/A";

                String hora = consulta.getHorarioConsultaString() != null ?
                        consulta.getHorarioConsultaString() : "N/A";

                String endereco = "Ruiz " + (consultas.indexOf(consulta) % 2 == 0 ? "XYZ" : "ABC");

                tabela.append(consulta.getPaciente().getNome()).append("\t")
                        .append(data).append("\t")
                        .append(hora).append("\t")
                        .append(endereco).append("\n");
            }

            return Response.ok(tabela.toString()).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao gerar tabela: " + e.getMessage())
                    .build();
        }
    }

    @GET
    public Response listarTodas() {
        try {
            List<Consulta> consultas = consultaRepository.listarTodas();
            return Response.ok(consultas).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar consultas")
                    .build();
        }
    }

    @GET
    @Path("/cuidador/{idCuidador}")
    public Response listarPorCuidador(@PathParam("idCuidador") Long idCuidador) {
        try {
            List<Consulta> consultas = consultaRepository.listarPorCuidador(idCuidador);
            return Response.ok(consultas).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar consultas do cuidador")
                    .build();
        }
    }
}