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


    public EventLogger(Long id, String date, LoggingActions action, String subject, String object, String path) {
        this.id = id;
        this.date = date;
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }
}
