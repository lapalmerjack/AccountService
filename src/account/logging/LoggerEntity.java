package account.logging;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name="events_log")
@AllArgsConstructor
@NoArgsConstructor
public class LoggerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logging_id")
    private Long id;

    private String date;

    private LoggingActions action;

    public String subject;

    public String object;

    public String path;

    private LoggerEntity(Builder builder) {
        this.date = builder.date;
        this.action = builder.action;
        this.subject = builder.subject;
        this.object = builder.object;
        this.path = builder.path;

    }

    public static class Builder {

        private String date;
        private LoggingActions action;
        private String subject;
        private String object;
        private String path;


        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder action(LoggingActions action) {
            this.action = action;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder object(String object) {
            this.object = object;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        // Build method to create an instance of the outer class
        public LoggerEntity build() {
            return new LoggerEntity(this);
        }
    }

    @Override
    public String toString() {
        return "LoggerEntity{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", action=" + action +
                ", subject='" + subject + '\'' +
                ", object='" + object + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
