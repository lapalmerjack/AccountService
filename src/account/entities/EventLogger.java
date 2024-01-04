package account.entities;

import account.entities.enums.LoggingActions;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Entity
@Table(name="events_log")
public class EventLogger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logging_id")
    private Long id;

    private String date;

    private LoggingActions action;

    public String subject;

    public String object;

    public String path;


}
