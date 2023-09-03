package annotation.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
        DadosExtraidos dadosExtraidos = this.extrairDados(ctx);
        String contrato     = dadosExtraidos.getContrato();

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
     * Retorna os dados extraidos como o numero do contrato
     * 
     * @param ctx
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private DadosExtraidos extrairDados(final InvocationContext ctx)
            throws IllegalArgumentException, IllegalAccessException {

        String contrato = null;

        //busca todos os parametroos do metodo
        Parameter[] parametros = ctx.getMethod().getParameters();
        int size = parametros.length;

        //Varre todos os parametros em busca daquele com a anotacao @ParamBody ou @ParametroContrato.
        //Se encontrar, pega o valor do parametro
        parametros: for(int i = 0; i < size; i++){
            if(parametros[i].isAnnotationPresent(ParametroContrato.class)){
                contrato = (String) ctx.getParameters()[i];
            }else if(parametros[i].isAnnotationPresent(ParamBody.class)){
                ParamBody annont = parametros[i].getAnnotation(ParamBody.class);

                //procura dentro do objeto o valor do contrato
                contrato = (String) this.getFieldValue(ctx.getParameters()[i], annont.pathFieldContrato());
            }
        }

        return DadosExtraidos.of(contrato);
    }

    /**
     * Retorna o valor do field presente em um determinado objeto
     * 
     * @param obj objeto a ser avaliado
     * @param pathField caminho do field que se deseja retornar o valor
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private Object getFieldValue(final Object obj, String pathField)
            throws IllegalArgumentException, IllegalAccessException {

        if(pathField.trim().isEmpty()){
            return null;
        }

        List<String> pathParts = null;

        if(pathField.indexOf(".") == -1){
            pathParts = Arrays.asList(pathField);
        }else{
            String[] split = pathField.split("\\.");
            pathParts = Arrays.asList( split );
        }            

        Object fieldValue = obj;
        for(String pathPart : pathParts){

            Class<? extends Object> fieldClass = null;

            if(fieldValue.getClass() != null){
                fieldClass = fieldValue.getClass();
            }

            Field declaredField = null;

            while(fieldClass != null){
                try{
                    declaredField = fieldClass.getDeclaredField(pathPart);
                    break;
                }catch(NoSuchFieldException e){
                    fieldClass = fieldClass.getSuperclass();
                }
            }

            if(declaredField != null){
                declaredField.setAccessible(true);
                fieldValue = declaredField.get(fieldValue);
            }else{
                fieldValue = null;
            }
        }

        return fieldValue;
    }
}

class DadosExtraidos{

    private String contrato;

    private DadosExtraidos(String contrato){
        this.contrato = contrato;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public static DadosExtraidos of(String contrato){
        return new DadosExtraidos(contrato);
    }
}