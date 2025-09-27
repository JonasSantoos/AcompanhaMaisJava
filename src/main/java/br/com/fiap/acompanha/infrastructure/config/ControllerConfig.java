package br.com.fiap.acompanha.infrastructure.config;
import br.com.fiap.acompanha.domain.service.CuidadorService;
import br.com.fiap.acompanha.interfaces.CuidadorController;
import br.com.fiap.acompanha.interfaces.CuidadorControllerImpl;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ControllerConfig {

    @ApplicationScoped
    public CuidadorController cuidadorController(CuidadorService cuidadorService){
        return new CuidadorControllerImpl(cuidadorService);
    }


}
