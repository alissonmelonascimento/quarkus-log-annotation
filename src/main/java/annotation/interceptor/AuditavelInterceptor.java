package annotation.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import annotation.Auditavel;
import annotation.Auditavel.ParametroContrato;
import annotation.ParamBody;
import entity.Logs;
import services.AuditoriaService;


@Auditavel
@Interceptor
public class AuditavelInterceptor {

    @Inject
    AuditoriaService auditoriaService;
    
    @AroundInvoke
    public Object auditar(final InvocationContext ctx) throws Exception{
        return execute(ctx);
    }

    Object execute(final InvocationContext ctx) throws Exception{

        Auditavel auditavel = this.getAuditavel(ctx.getMethod());
        String contrato     = this.getContrato(ctx);

        System.out.println("Tipo de operacao: "+auditavel.tipoOperacao().name());
        System.out.println("Contrato: "+contrato);

        Logs logs     = new Logs();
        logs.ini      = LocalDateTime.now();
        logs.operacao = auditavel.tipoOperacao().getCode();
        logs.nome     = auditavel.tipoOperacao().getValue();
        logs.contrato = contrato;
        logs.sucesso  = false;

        try{
            //iniciando auditoria
            logs = auditoriaService.iniciaAuditoria(logs);

            //executando metodo
            Object result = ctx.proceed();

            //finaliza a auditoria indicando sucesso na chamada do servico
            auditoriaService.finalizaAuditoria(logs.id, true);

            return result;
        }catch(Exception e){

            /**
             * finaliza a auditoria indicando sucesso na chamada do servico,
             * caso a excecao esteja entre aquelas consideradas para sucesso
             */
            for(Class<? extends Throwable> t : auditavel.consideraSucessoPara()){
                if(t.isInstance(e)){
                    auditoriaService.finalizaAuditoria(logs.id, true);
                    throw e;
                }
            }

            //finaliza a auditoria indicando erro na chamada do servico
            auditoriaService.finalizaAuditoria(logs.id, false);
            throw e;
        }

    }

    /**
     * Retorna a anotacao Auditavel usada no metodo
     * 
     * @param m
     * @return
     */
    private Auditavel getAuditavel(final Method m){
        Auditavel[] auditaveis = m.getAnnotationsByType(Auditavel.class);

        if(auditaveis != null){
            return auditaveis[0];
        }

        return null;
    }    

    /**
     * Retorna o valor de um parametro de um metodo
     * 
     * @param ctx
     * @param annotationClass
     * @return
     */
    private Object getParamValue(final InvocationContext ctx, Class<? extends Annotation> annotationClass) {

        //busca todos os parametroos do metodo
        Parameter[] parametros = ctx.getMethod().getParameters();
        int size = parametros.length;

        //varre todos os parametros em busca daquele com a anotacao annotationClass
        parametros: for(int i = 0; i < size; i++){

            // se encontrar o parametro anotado, busca seu valor no contexto(ctx) baseado na
            // posicao de declaracao do parametro
            if(parametros[i].isAnnotationPresent(annotationClass)){
                Object[] parameters = ctx.getParameters();//o valor do parametro fica no contexto, nao no field
                return parameters[i];//retornando o valor do parametro
            }
        }

        return null;
    }

    /**
     * Retorna um Map onde o key é o path do field e o value o valor do body
     * 
     * @param ctx
     * @return
     */
    private Map<String, Object> getBodyValue(final InvocationContext ctx){

        //busca todos os parametroos do metodo
        Parameter[] parametros = ctx.getMethod().getParameters();
        int size = parametros.length;

        //varre todos os parametros em busca daquele com a anotacao @ParamBody
        parametros: for(int i = 0; i < size; i++){

            // se encontrar o parametro anotado, busca seu valor no contexto(ctx) baseado na
            // posicao de declaracao do parametro
            if(parametros[i].isAnnotationPresent(ParamBody.class)){
                Object[] parameters = ctx.getParameters();//o valor do parametro fica no contexto, nao no field

                Map<String, Object> map = new HashMap<>();
                ParamBody annont = parametros[i].getAnnotation(ParamBody.class);
                map.put(annont.pathFieldContrato(), parameters[i]);

                return map;//retornando o path do codigo do contrato e o valor do body
            }
        }

        return null;
    }

    /**
     * Retorna o valor do field presente em um determinado objeto
     * 
     * @param obj objeto a ser avaliado
     * @param pathField caminho do field que se deseja retornar o valor
     * @return
     */
    private Object getFieldValue(final Object obj, String pathField){

        if(pathField.trim().isEmpty()){
            return null;
        }

        try{
            List<String> pathParts = null;

            if(pathField.indexOf(".") == -1){
                pathParts = Arrays.asList(pathField);
            }else{
                String[] split = pathField.split("\\.");
                pathParts = Arrays.asList( split );
            }            

            Object fieldValue = obj;
            for(String pathPart : pathParts){
                Field declaredField = fieldValue.getClass().getDeclaredField(pathPart);
                declaredField.setAccessible(true);
                fieldValue = declaredField.get(fieldValue);
            }

            return fieldValue;
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retorna o valor do contrato, se tiver
     * 
     * @param ctx
     * @return
     */
    private String getContrato(final InvocationContext ctx){

        //buscando valor do contrato se estiver entre os parametros do metodo
        String valorContrato = (String)this.getParamValue(ctx, ParametroContrato.class);
        if(valorContrato != null){
            return valorContrato;
        }

        //buscando valor do contrato se estiver no body de entrada
        Map<String, Object> bodyMap = this.getBodyValue(ctx);
        if(bodyMap != null){

            return (String) this.getFieldValue(bodyMap.entrySet().iterator().next().getValue(),
                                      bodyMap.entrySet().iterator().next().getKey());
        }        

        return null;
    }    
    
}