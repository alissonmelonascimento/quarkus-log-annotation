package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ParamBody {

    /**
     * Caminho do field onde se encontra o codigo do contrato, 
     * para permitir a navegacao pela árvore de objetos ate encontrar o valor envolvido.
     * 
     * <pre>
     * //Exemplo 1: identificador do contrato na raiz do objeto
     * public class Contrato{
     *     private String codigo; //codigo do contrato
     *     private String descricao;
     * }
     * </pre>
     * 
     * Informe assim: 
     * 
     * <pre>
     * public void salvar(@ParamBody(pathFieldContrato = "codigo") Contrato body)
     * </pre>
     * 
     * 
     * <pre>
     * //Exemplo 2: identificador do contrato em objeto interno
     * public class Deposito{
     *     private Contrato contrato;//codigo do contrato pertence a este objeto
     *     private Double valor;
     * }
     * </pre>
     * 
     * Informe assim:
     * <pre>
     * public void salvar(@ParamBody(pathFieldContrato = "contrato.codigo") Deposito body)
     * </pre>
     * 
     * @return o valor do field com o codigo do contrato
     */
    @Nonbinding String pathFieldContrato() default "";
    
}
