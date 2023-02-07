package services;

import entity.Logs;

public interface AuditoriaService {

    Logs iniciaAuditoria(Logs logs);
    Logs finalizaAuditoria(Long id, boolean sucesso);
    
}
