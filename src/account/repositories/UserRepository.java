package account.repositories;

import account.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT s FROM User s WHERE s.email = ?1")
    Optional<User> findByEmailIgnoreCase(String email);


    @Query("SELECT s FROM User s WHERE s.name = ?1")
    Optional<User> findByName(String name);


    Optional<User>deleteByEmail(String email);




    @Query("UPDATE User u SET u.loginAttempts = :failedAttempts WHERE u.email = :email")
    @Modifying
    void updateFailedAttempts(@Param("failedAttempts") int failedAttempts, @Param("email") String email);



}