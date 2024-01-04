package account.repositories;

import account.entities.EventLogger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoggerRepository extends JpaRepository<EventLogger, Long> {
}
