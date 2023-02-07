package entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;


@Entity
public class Logs extends PanacheEntityBase{

    @Id
    @SequenceGenerator(
        name = "SQ_LOGS",
        sequenceName = "SQ_LOGS",
        allocationSize = 1,
        initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_LOGS")
    public Long id;

    public String nome;
    public LocalDateTime ini;
    public LocalDateTime fim;
    public boolean sucesso;
    public Integer operacao;
    public String contrato;
    
}