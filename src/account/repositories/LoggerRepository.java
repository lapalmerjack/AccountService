package account.repositories;

import account.entities.EventLogger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LoggerRepository extends JpaRepository<EventLogger, Long> {
}
