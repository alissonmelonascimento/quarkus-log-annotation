package enums;

public enum TipoOperacaoEnum {

    NULL(1,"nulo"),
    BUSCA_CONTRATOS(1,"busca-contratos-service"),
    DETALHA_CONTRATO(3,"detalha-contrato-service");

    private Integer code;
    private String value;

    TipoOperacaoEnum(Integer code, String value){
        this.code  = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
    
}
