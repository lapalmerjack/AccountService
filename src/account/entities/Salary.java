package account.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;


@Getter
@Setter
@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor

public class Salary {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "salary_id")
    private Long Id;

    @NonNull
    @Column(name = "employee")
    private String employee;

    @NonNull
    @Column(name = "period")
    @Pattern(regexp = "^(0[1-9]|1[0-2])-(20\\d{2})$", message = "Invalid date")
    private String period;

    @NonNull
    @Column(name = "salary")
    private Long salary;

    @ManyToOne(cascade = {CascadeType.DETACH,
            CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Override
    public String toString() {
        return "SalaryPayments{" +
                "Id=" + Id +
                ", employee='" + employee + '\'' +
                ", period='" + period + '\'' +
                ", salary=" + salary +

                '}';
    }
}
