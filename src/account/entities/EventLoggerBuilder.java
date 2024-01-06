package account.entities;

import account.entities.enums.LoggingActions;
import jakarta.persistence.*;


@Entity
public class EventLoggerBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logging_id")
    private Long id;
    private String date;
    private LoggingActions action;
    private String subject;
    private String object;
    private String path;

    public EventLoggerBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public EventLoggerBuilder setDate(String date) {
        this.date = date;
        return this;
    }

    public EventLoggerBuilder setAction(LoggingActions action) {
        this.action = action;
        return this;
    }

    public EventLoggerBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public EventLoggerBuilder setObject(String object) {
        this.object = object;
        return this;
    }

    public EventLoggerBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public EventLogger createEventLogger() {
        return new EventLogger(id, date, action, subject, object, path);
    }
}