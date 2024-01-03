package account.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank @NotNull
    String name;

    @NotBlank  @NotNull
    String lastname;

    @NotBlank    @NotNull   @Email(regexp = ".+@acme.com$")
    String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank @NonNull
    @Size(min = 12,
            message = "Password length must be 12 chars minimum!" )
    String password;

    @OneToMany(mappedBy="user", cascade = {CascadeType.ALL})
    @JsonIgnore
    private List<Salary> salaries = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    public User (String name, String lastname, String email, @NonNull String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public User(Long id, String name, String lastname, String email, @NonNull String password, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public void addPayment(Salary payments) {
        if (salaries == null) {
            salaries = new ArrayList<>();
        }
        System.out.println("ADDING PAYMENT: " + payments);
        salaries.add(payments);

        payments.setEmployee(this.email);
    }

    public void addRole(Role role) {
       if (roles == null) {
           roles = new HashSet<>();
       }
       roles.add(role);
    }

    }