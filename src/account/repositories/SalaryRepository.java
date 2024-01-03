package account.repositories;

import account.entities.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
//    @Query("SELECT s FROM Payments s WHERE s.email = ?1")

//
//    @Query("SELECT s FROM Payments s WHERE s.email = ?1")
//    List<Optional<SalaryPayments>> findPaymentsByEmailIgnoreCase(String email);

     @Query("Select s FROM Salary s WHERE s.employee = ?1 AND s.period =?2")
     Optional<Salary>  findSalaryByEmailAndPeriod(String email, String period);

}
