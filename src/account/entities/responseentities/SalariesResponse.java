package account.entities.responseentities;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SalariesResponse {

    private String name;
    private String lastname;
    private String period;
    private String salary;



}
