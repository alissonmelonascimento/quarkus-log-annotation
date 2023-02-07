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
import repository.LogsRepository;
import services.AuditoriaService;


@Auditavel
@Interceptor
public class AuditavelInterceptor {

    @Inject
    AuditoriaService auditoriaService;

    @Inject
    LogsRepository logsRepository;

    
    @AroundInvoke
    public Object myMethod(InvocationContext ctx) throws Exception{
        return execute(ctx);
    }

    Object execute(InvocationContext ctx) throws Exception{

        Auditavel auditavel = getAuditavel(ctx.getMethod());
        String contrato     = getContrato(ctx);

        System.out.println("Tipo de operacao: "+auditavel.tipoOperacao().name());
        System.out.println("Contrato: "+contrato);

        Logs logs    = new Logs();
        logs.ini     = LocalDateTime.now();
        logs.nome    = "teste";
        logs.sucesso = false;
        logs.operacao = auditavel.tipoOperacao().getCode();
        logs.contrato = contrato;

        Object result = null;
        try{
            //iniciando auditoria
            logs = auditoriaService.iniciaAuditoria(logs);

            //executando metodo
            result = ctx.proceed();

            auditoriaService.finalizaAuditoria(logs.id, true);

            return result;
        }catch(Exception e){
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

        System.out.println(">>> buscando valor do contrato");
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
