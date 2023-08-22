package api.request;

public class DepositoRequestBody {

    private ContratoRequestBody contrato;    
    private Double valor;

    public ContratoRequestBody getContrato() {
        return contrato;
    }

    public void setContrato(ContratoRequestBody contrato) {
        this.contrato = contrato;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
    
}
