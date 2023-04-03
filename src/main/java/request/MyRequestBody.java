package request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MyRequestBody {

    @JsonProperty(value = "nome")
    public String nome;

    @JsonProperty(value = "sobre_nome")
    public String sobreNome;
    
}
