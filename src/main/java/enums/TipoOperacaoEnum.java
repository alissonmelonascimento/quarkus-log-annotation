package enums;

public enum TipoOperacaoEnum {

    NULL(1,"nulo","null"),
    BUSCA_CONTRATOS(1,"busca-contratos-service","/contratos"),
    DETALHA_CONTRATO(3,"detalha-contrato-service","/contratos"),
    SALVA(3,"salvar-service","/contratos");

    private Integer code;
    private String value;
    private String url;

    TipoOperacaoEnum(Integer code, String value, String url){
        this.code  = code;
        this.value = value;
        this.url   = url;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getUrl() {
        return url;
    }
    
}
