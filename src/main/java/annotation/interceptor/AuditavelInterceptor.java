package annotation.interceptor;

import java.time.LocalDateTime;
import java.util.Map.Entry;

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

        for (Entry<String, Object> e : ctx.getContextData().entrySet()){
            System.out.println("KEY: "+e.getKey());
            System.out.println("VALUE: "+e.getValue().getClass());
        }

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
    
}
