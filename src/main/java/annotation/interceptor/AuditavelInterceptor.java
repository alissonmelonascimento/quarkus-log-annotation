package annotation.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.TransactionManager;

import annotation.Auditavel;
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

        /*System.out.println(">>> ctx.getContextData().entrySet()");
        for (Entry<String, Object> e : ctx.getContextData().entrySet()){
            System.out.println("KEY: "+e.getKey());
            System.out.println("VALUE: "+e.getValue().getClass());
        }*/

        Auditavel auditavel = getAuditavelAnnotation(ctx.getMethod());
        System.out.println("Tipo de auditoria: "+auditavel.tipoOperacao().name());

        //ctx.getContextData().entrySet().iterator().next().getValue();

        Logs logs = new Logs();
        logs.ini = LocalDateTime.now();
        logs.nome = "teste";
        logs.sucesso = false;

        tm.begin();

        logsRepository.persist(logs);

        Object result = ctx.proceed();

        logs.fim = LocalDateTime.now();
        logs.sucesso = true;
        
        logsRepository.persist(logs);
        
        tm.commit();

        return result;
    }

    Auditavel getAuditavelAnnotation(Method m){
        System.out.println(">>> Annotations");
        for(Annotation a : m.getAnnotations()){
            //System.out.println(a.toString());
            if(a instanceof Auditavel){
                System.out.println("RETORNANDO: "+a.toString());
                return (Auditavel) a;
            }
        }

        System.out.println(">>> DeclaringClass Annotations");
        for(Annotation a : m.getDeclaringClass().getAnnotations() ){
            if(a instanceof Auditavel){
                System.out.println("RETORNANDO: "+a.toString());
                return (Auditavel) a;
            }
        }

        throw new RuntimeException("@Auditavel not found on method " + m.getName() +
                " or its class " + m.getClass().getName());
    }
    
}
