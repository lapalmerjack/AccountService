package account.services;

import account.entities.Salary;
import account.entities.User;
import account.entities.responseentities.SalariesResponse;
import account.error.customexceptions.payments.DateSyntaxWrongException;
import account.error.customexceptions.payments.ExistingDatePeriodException;
import account.error.customexceptions.payments.NoExistingDatePeriodException;
import account.error.customexceptions.payments.SalaryBelowZeroException;
import account.error.customexceptions.users.UserNotFoundException;
import account.repositories.SalaryRepository;
import account.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private final SalaryRepository salaryRepository;

    @Autowired
    private final UserRepository userRepository;

    public PaymentService(SalaryRepository salaryRepository, UserRepository userRepository) {
        this.salaryRepository = salaryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void registerAllSalaryPayments

            (List<Salary> salary) {

        salary.forEach(this::registerSalaryPayment);

    }

    public List<SalariesResponse> getPayments(String email) {

        User user = retrieveUser(email);
        return returnSalaries(user.getSalaries(), user);

    }

    public SalariesResponse getPayment(UserDetails user, String period) {
        User fetchedUser = retrieveUser(user.getUsername());
        checkPeriodSyntax(period);

        Salary salary = fetchedUser.getSalaries().stream()
                .filter(s -> s.getPeriod()
                        .equals(period))
                .findFirst()
                .orElseThrow(NoExistingDatePeriodException::new);

        String formattedSalary = formatSalaryResponse(salary.getSalary());
        String formattedPeriod = formatPeriodDateResponse(salary.getPeriod());

        return new SalariesResponse(fetchedUser.getName(),
                fetchedUser.getLastname(), formattedPeriod, formattedSalary);


    }


    private List<SalariesResponse> returnSalaries(List<Salary> salaries, User user) {

        List<SalariesResponse> returnedSalaries = new ArrayList<>();

        for (Salary salary : salaries) {
            String formattedSalary = formatSalaryResponse(salary.getSalary());
            String formattedPeriod = formatPeriodDateResponse(salary.getPeriod());

            returnedSalaries.add(new SalariesResponse(
                    user.getName(),
                    user.getLastname(),
                    formattedPeriod,
                    formattedSalary));
        }
        Collections.reverse(returnedSalaries);


        return returnedSalaries;
    }


    private String formatSalaryResponse(Long salary) {
        return String.format("%d dollar(s) %d cent(s)"
                , salary / 100, salary % 100);
    }

    private String formatPeriodDateResponse(String period) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-yyyy");

        YearMonth date = YearMonth.parse(period, dateTimeFormatter);

        String monthName = date.getMonth().toString().charAt(0) +
                date.getMonth().toString().substring(1).toLowerCase();

        return monthName + "-" + date.getYear();

    }

    @Transactional
    public void returnUpdatedSalary(Salary salary) {
        LOGGER.info("Retrieving user for salary update");

        User user = retrieveUser(salary.getEmployee());
        CarryoutInformationChecks(salary);

        Salary oldSalary = salaryRepository.findSalaryByEmailAndPeriod(salary.getEmployee()
                        , salary.getPeriod())
                .orElseThrow(NoExistingDatePeriodException::new);

        salary.setId(oldSalary.getId());
        salary.setUser(user);
        user.addPayment(salary);

        userRepository.save(user);
        LOGGER.info("Salary payment updated");

    }

    private void registerSalaryPayment(Salary salary) {
        LOGGER.info("retrieving employee payslips");

        User user = retrieveUser(salary.getEmployee());
        CarryoutInformationChecks(salary);
        checkIdenticalDates(user.getSalaries(), salary.getPeriod());

        salary.setUser(user);
        user.addPayment(salary);

        userRepository.save(user);
        LOGGER.info("Salary payment saved");
    }

    private void CarryoutInformationChecks(Salary salary) {
        checkPeriodSyntax(salary.getPeriod());
        checkSalaryIsAboveZero(salary.getSalary());
        checkSalaryIsAboveZero(salary.getSalary());
    }

    private void checkSalaryIsAboveZero(Long salary) {
        if (salary < 0) {
            LOGGER.error("Salary is below zero: " + salary);
            throw new SalaryBelowZeroException();
        }

    }

    private void checkIdenticalDates(List<Salary> salaryPayments, String date) {
        boolean match = salaryPayments.stream().anyMatch(s -> s.getPeriod().equals(date));
        if (match) {
            LOGGER.error("Identical date found");
            throw new ExistingDatePeriodException();
        }
        LOGGER.info("No identical dates found");
    }

    private void checkPeriodSyntax(String date) {
        String regex = "^(0[1-9]|1[0-2])-(20\\d{2})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        if (!matcher.matches()) {
            LOGGER.error("Wrong syntax in date: " + date);
            throw new DateSyntaxWrongException();
        }
    }

    private User retrieveUser(String email) {
        return userRepository
                .findByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);
    }

}