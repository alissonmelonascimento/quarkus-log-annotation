package api;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import api.request.DepositoRequestBody;

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
    @Consumes(MediaType.APPLICATION_JSON)
    public void salvar(DepositoRequestBody body);
    
}
