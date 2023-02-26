package repository;

import entity.Logs;

public interface LogsRepository{

    void salvar(Logs logs);
    Logs buscaPeloId(Long id);
    
}
