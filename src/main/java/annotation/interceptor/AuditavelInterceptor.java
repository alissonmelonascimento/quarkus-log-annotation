package annotation.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.TransactionManager;

import annotation.Auditavel;
import annotation.Auditavel.ParametroContrato;
import entity.Logs;
import repository.LogsRepository;


@Auditavel
@Interceptor
public class AuditavelInterceptor {

    @Inject TransactionManager tm;

    @Inject
    LogsRepository logsRepository;

    
    @AroundInvoke
    public Object myMethod(InvocationContext ctx) throws Exception{
        return execute(ctx);
    }

    Object execute(InvocationContext ctx) throws Exception{

        Auditavel auditavel = getAuditavel(ctx.getMethod());
        String contrato     = getContrato(ctx.getMethod());

        System.out.println("Tipo de operacao: "+auditavel.tipoOperacao().name());
        System.out.println("Contrato: "+contrato);

        Logs logs    = new Logs();
        logs.ini     = LocalDateTime.now();
        logs.nome    = "teste";
        logs.sucesso = false;
        logs.operacao = auditavel.tipoOperacao().getCode();
        logs.contrato = contrato;

        tm.begin();

        logsRepository.persist(logs);

        Object result = ctx.proceed();

        logs.fim     = LocalDateTime.now();
        logs.sucesso = true;
        
        logsRepository.persist(logs);
        
        tm.commit();

        return result;
    }


    Auditavel getAuditavel(Method m){
        Auditavel[] auditaveis = m.getAnnotationsByType(Auditavel.class);

        if(auditaveis != null){
            return auditaveis[0];
        }

        return null;
    }

    String getContrato(Method m){

        System.out.println("*** Listando parametros do metodo");
        for(Parameter parameter : m.getParameters()){
            System.out.println("Param: "+parameter);
        }


        Annotation[][] parameterAnnotations = m.getParameterAnnotations();
        int size = parameterAnnotations.length;

        System.out.println(">>> buscando valor do contrato");
        String contrato = null;
        parametros: for(int i = 0; i < size; i++){
            Annotation[] annotations = parameterAnnotations[i];
            for(Annotation annotation : annotations){
                if(annotation instanceof ParametroContrato){
                    Parameter[] parameters = m.getParameters();
                    System.out.println("Valor do ParametroContrato: "+parameters[i]);
                    contrato = (String) (Object) parameters[i];
                    break parametros;
                }
            }
        }

        return contrato;
    }
    
}
