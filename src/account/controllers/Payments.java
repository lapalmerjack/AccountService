package account.controllers;

import account.entities.Salary;
import account.entities.User;
import account.entities.responseentities.SalariesResponse;
import account.security.UserDetailsImpl;
import account.services.PaymentService;
import account.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class Payments {

    @Autowired
    private final PaymentService paymentService;



    private static final Logger LOGGER = LoggerFactory.getLogger(Payments.class);

    public Payments (PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/api/empl/payment")
    public ResponseEntity <?> getPayments(@Valid @AuthenticationPrincipal UserDetailsImpl user,
                                          @RequestParam(name="period", required = false) String period) {
        LOGGER.info("Retrieving user details");
        List<SalariesResponse> salaries;
        if(period == null) {
            salaries = paymentService.getPayments(user.getUsername());
            return new ResponseEntity<>(salaries, HttpStatus.OK);
        }

        SalariesResponse response = paymentService.getPayment(user, period);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority('ROLE_ACCOUNTANT')")
    @PutMapping("/api/acct/payments")
    public ResponseEntity<Map<String, String>> changeSalary(@RequestBody Salary salary) {
        LOGGER.info("Changing salary" );

        paymentService.returnUpdatedSalary(salary);


        return new ResponseEntity<>(Map.of("status", "Updated successfully!"), HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority('ROLE_ACCOUNTANT')")
    @PostMapping("/api/acct/payments")
    public ResponseEntity<Map<String, String>> registerPayment(@RequestBody List<Salary> salaryPayments) {
        LOGGER.info("IN THE AUTO ZONE!");
        salaryPayments.forEach(s -> LOGGER.info("MY PAYMENTS " +  s.getSalary()));

        paymentService.registerAllSalaryPayments(salaryPayments);

        return new ResponseEntity<>(Map.of("status", "Added successfully!"), HttpStatusCode.valueOf(200));
    }

}