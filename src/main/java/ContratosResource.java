import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import annotation.Auditavel;
import annotation.Auditavel.ParametroContrato;
import enums.TipoOperacaoEnum;

@Path("/contratos")
public class ContratosResource {

    @Auditavel(tipoOperacao = TipoOperacaoEnum.BUSCA_CONTRATOS)
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String buscar() {
        return "Contratos";
    }

    @Auditavel(tipoOperacao = TipoOperacaoEnum.DETALHA_CONTRATO, consideraSucessoPara = {RuntimeException.class})
    @GET
    @Path("{contrato}")
    @Produces(MediaType.TEXT_PLAIN)
    public String buscar2(@ParametroContrato @PathParam("contrato") String contrato) {
        //throw new RuntimeException("Erro");
        return "Contrato '"+contrato+"' detalhado";
    }    
    
}
