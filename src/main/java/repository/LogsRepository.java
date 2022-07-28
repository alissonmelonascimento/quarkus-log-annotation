package repository;

import entity.Logs;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

public interface LogsRepository extends PanacheRepositoryBase<Logs, Long>{
    
}
