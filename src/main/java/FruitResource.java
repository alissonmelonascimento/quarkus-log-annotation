import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import annotation.Auditavel;
import enums.TipoOperacaoEnum;

@Path("/fruits")
public class FruitResource {

    @Auditavel(tipoOperacao = TipoOperacaoEnum.BUSCAR_FRUTAS)
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String buscar() {
        return "Hello from RESTEasy Reactive";
    }
    
}
