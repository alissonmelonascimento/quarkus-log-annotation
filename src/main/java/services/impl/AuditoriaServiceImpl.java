package services.impl;

import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import entity.Logs;
import repository.LogsRepository;
import services.AuditoriaService;

@ApplicationScoped
public class AuditoriaServiceImpl implements AuditoriaService{

    @Inject
    LogsRepository logsRepository;

    @Override
    @Transactional(value = Transactional.TxType.REQUIRED)
    public Logs iniciaAuditoria(Logs logs) {
        logs.ini = LocalDateTime.now();
        logsRepository.salvar(logs);
        return logs;
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public Logs finalizaAuditoria(Long id, boolean sucesso) {

        //evita o erro de detached object
        Logs logs = logsRepository.buscaPeloId(id);
        logs.sucesso = sucesso;
        logs.fim = LocalDateTime.now();
        logsRepository.salvar(logs);
        return logs;
    }
    
}
