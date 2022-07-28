package enums;

public enum TipoOperacaoEnum {

    NULL(1,"nulo"),
    BUSCAR_FRUTAS(1,"buscar-frutas-service"),
    DETALHA_FRUTA(3,"detalha-fruta-service");

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
