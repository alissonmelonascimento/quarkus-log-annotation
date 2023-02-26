import annotation.Auditavel;
import annotation.Auditavel.ParametroContrato;
import enums.TipoOperacaoEnum;

public class ContratosResource implements IContratosResource{

    @Auditavel(tipoOperacao = TipoOperacaoEnum.BUSCA_CONTRATOS)
    public String buscar() {
        return "Contratos";
    }

    @Auditavel(tipoOperacao = TipoOperacaoEnum.DETALHA_CONTRATO, consideraSucessoPara = {RuntimeException.class})
    public String detalhar(@ParametroContrato String contrato) {
        throw new RuntimeException("Erro");
        //return "Contrato '"+contrato+"' detalhado";
    }    
    
}