package annotation.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import annotation.Auditavel;
import annotation.Auditavel.ParamBody;
import annotation.Auditavel.ParamFieldContrato;
import annotation.Auditavel.ParametroContrato;
import entity.Logs;
import services.AuditoriaService;


@Auditavel
@Interceptor
public class AuditavelInterceptor {

    @Inject
    AuditoriaService auditoriaService;
    
    @AroundInvoke
    public Object myMethod(final InvocationContext ctx) throws Exception{
        return execute(ctx);
    }

    Object execute(final InvocationContext ctx) throws Exception{

        Auditavel auditavel = getAuditavel(ctx.getMethod());
        String contrato     = getContrato(ctx);

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

    Object getParamValue(final InvocationContext ctx, Class<? extends Annotation> annotationClass){

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

    Auditavel getAuditavel(final Method m){
        Auditavel[] auditaveis = m.getAnnotationsByType(Auditavel.class);

        if(auditaveis != null){
            return auditaveis[0];
        }

        return null;
    }

    /**
     * Retorna o valor do contrato, se tiver
     * 
     * @param ctx
     * @return
     */
    String getContrato(final InvocationContext ctx){

        //buscando valor do contrato se estiver entre os parametros do metodo
        String valorContrato = (String)this.getParamValue(ctx, ParametroContrato.class);
        if(valorContrato != null){
            return valorContrato;
        }

        //buscando valor do contrato se estiver no body de entrada
        Object bodyValue = this.getBodyValue(ctx);
        if(bodyValue != null){
            return this.getFieldValue(bodyValue, ParamFieldContrato.class);
        }

        return null;
    }

    /**
     * Retornando o objeto que representa o body de entrada
     * 
     * @param ctx
     * @return
     */
    Object getBodyValue(final InvocationContext ctx){

        /*if(!ctx.getMethod().isAnnotationPresent(ParamBody.class)){
            return null;
        }*/

        //busca todos os parametroos do metodo
        Parameter[] parametros = ctx.getMethod().getParameters();
        int size = parametros.length;

        //varre todos os parametros em busca daquele com a anotacao @ParamBody
        parametros: for(int i = 0; i < size; i++){

            // se encontrar o parametro anotado, busca seu valor no contexto(ctx) baseado na
            // posicao de declaracao do parametro
            if(parametros[i].isAnnotationPresent(ParamBody.class)){
                Object[] parameters = ctx.getParameters();//o valor do parametro fica no contexto, nao no field
                return parameters[i];//retornando o valor do body
            }
        }

        return null;
    }

    /**
     * Retornando o valor do field de um determinado objeto
     * 
     * @param body
     * @param annotationClass
     * @return
     */
    String getFieldValue(final Object body, Class<? extends Annotation> annotationClass){

        // FIXME colocar uma chamada recursiva para navegar na arvore de
        // objetos/valores, pois a anotacao pode estar em um objeto interno
        
        String valor = null;
        Field[] declaredFields = body.getClass().getDeclaredFields();
        int size = declaredFields.length;
        declaredFields: for(int i = 0; i < size; i++){
            if(declaredFields[i].isAnnotationPresent(annotationClass)){                
                try {
                    //body.getClass().getMethods()[i].invoke(null);

                    //tenta pegar o valor do field
                    valor = (String) declaredFields[i].get(body);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {

                    try {
                        //se o field nao for acessivel (private, protected, default)
                        //alterar seu acesso para true e pega o valor
                        declaredFields[i].setAccessible(true);
                        valor = (String) declaredFields[i].get(body);
                    } catch (IllegalArgumentException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    }

                    declaredFields[i].setAccessible(false);
                }
            }
        }

        return valor;
    }
    
}