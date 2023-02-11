package annotation.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import annotation.Auditavel;
import annotation.Auditavel.ParametroContrato;
import entity.Logs;
import services.AuditoriaService;


@Auditavel
@Interceptor
public class AuditavelInterceptor {

    @Inject
    AuditoriaService auditoriaService;
    
    @AroundInvoke
    public Object myMethod(InvocationContext ctx) throws Exception{
        return execute(ctx);
    }

    Object execute(InvocationContext ctx) throws Exception{

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


    Auditavel getAuditavel(Method m){
        Auditavel[] auditaveis = m.getAnnotationsByType(Auditavel.class);

        if(auditaveis != null){
            return auditaveis[0];
        }

        return null;
    }

    String getContrato(InvocationContext ctx){

        Annotation[][] parameterAnnotations = ctx.getMethod().getParameterAnnotations();
        int size = parameterAnnotations.length;

        System.out.println(">>> Buscando valor do contrato");
        String contrato = null;
        parametros: for(int i = 0; i < size; i++){
            Annotation[] annotations = parameterAnnotations[i];
            for(Annotation annotation : annotations){
                if(annotation instanceof ParametroContrato){
                    Object[] parameters = ctx.getParameters();
                    System.out.println("Valor do ParametroContrato: "+parameters[i]);
                    contrato = (String) parameters[i];
                    break parametros;
                }
            }
        }

        return contrato;
    }
    
}