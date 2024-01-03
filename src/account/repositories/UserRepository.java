package account.repositories;

import account.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT s FROM User s WHERE s.email = ?1")
    Optional<User> findByEmailIgnoreCase(String email);


    @Query("SELECT s FROM User s WHERE s.name = ?1")
    Optional<User> findByName(String name);

    Optional<User>deleteByEmail(String email);


}