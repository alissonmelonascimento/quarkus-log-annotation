package repository.impl;

import javax.enterprise.context.ApplicationScoped;

import entity.Logs;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import repository.LogsRepository;


@ApplicationScoped
public class LogsRepositoryImpl implements LogsRepository, PanacheRepository<Logs>{

    public void salvar(final Logs logs){
        persist(logs);
    }

    @Override
    public Logs buscaPeloId(final Long id) {
        return findById(id);
    }
    
}
