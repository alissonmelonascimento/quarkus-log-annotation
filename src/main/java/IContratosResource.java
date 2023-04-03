import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import annotation.Auditavel.EntradaServico;
import request.MyRequestBody;

@Path("/contratos")
public interface IContratosResource {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String buscar();
    
    @GET
    @Path("{contrato}")
    @Produces(MediaType.TEXT_PLAIN)
    public String detalhar(@PathParam("contrato") String contrato);

    @POST
    public void salvar(MyRequestBody body);
    
}
